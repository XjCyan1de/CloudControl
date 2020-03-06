package com.github.xjcyan1de.cloudcontrol

import com.github.xjcyan1de.cloudcontrol.api.*
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNodeInfo
import com.github.xjcyan1de.cloudcontrol.api.node.NodeConfiguration
import com.github.xjcyan1de.cloudcontrol.api.service.ProcessSnapshot
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceLifeCycle
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceProvider
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTask
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ConfigurationManager
import com.github.xjcyan1de.cloudcontrol.api.util.SystemStatistics
import com.github.xjcyan1de.cloudcontrol.node.NodeManager
import com.github.xjcyan1de.cloudcontrol.service.CloudServiceManager
import com.github.xjcyan1de.cloudcontrol.service.NodeServiceProvider
import com.github.xjcyan1de.cyanlibz.messenger.Messenger
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

object CloudControlNode : CloudControlDriver {
    val processQueue = ConcurrentLinkedQueue<() -> Unit>()
    val serviceManager = CloudServiceManager()
    override val serviceProvider: ServiceProvider = NodeServiceProvider
    override lateinit var nodeConfiguration: NodeConfiguration
    override lateinit var messenger: Messenger
    override lateinit var currentNetworkNodeInfo: NetworkNodeInfo
    override lateinit var lastNetworkNodeInfo: NetworkNodeInfo

    override fun start() {
        nodeConfiguration = NodeConfiguration.load()
        currentNetworkNodeInfo = createNetworkNodeInfo()
        lastNetworkNodeInfo = currentNetworkNodeInfo

        ConfigurationManager.load()

        val tempDirectory = TEMP_DIR.toFile()
        tempDirectory.mkdirs()
        val localDirectory = LOCAL_DIR.toFile()
        localDirectory.mkdirs()

        File(tempDirectory, "caches").mkdir()

        Runtime.getRuntime().addShutdownHook(thread(name = "Shutdown Thread") { stop() })

        mainLoop()
    }

    override fun stop() {

    }

    fun runConsole() {

    }

    /**
     * Главный цикл приложения, который выполняется 100 раз в секунду
     */
    fun mainLoop() = runBlocking {
        var value = System.currentTimeMillis()
        val millis = 1000 / TPS.toLong()
        var launchServicesTimer = TPS
        val sendNodeUpdateTimer = 0 / 2

        while (true) {
            try {
                val diff = System.currentTimeMillis() - value
                if (diff < millis) {
                    try {
                        delay(millis - diff)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                value = System.currentTimeMillis()

                while (!processQueue.isEmpty()) {
                    if (processQueue.peek() != null) {
                        processQueue.poll().invoke()
                    } else {
                        processQueue.poll()
                    }
                }

                if (launchServicesTimer++ >= TPS) {
                    launchServices()
                    launchServicesTimer = 0
                }

                stopDeadServices()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun launchServices() {
        for (task in serviceManager.serviceTasks) {
            if (task.canStartServices()) {
                val taskServices = getServices(task)
                val runningServices = taskServices.filter { it.lifeCycle == ServiceLifeCycle.RUNNING }

                if (task.associatedNodes.isEmpty() ||
                    (task.associatedNodes.contains(nodeConfiguration.identity) && task.minServiceCount > runningServices.size)
                ) {
                    val notStartedService = serviceManager.getLocalCloudServices(task).find {
                        it.lifeCycle == ServiceLifeCycle.DEFINED || it.lifeCycle == ServiceLifeCycle.PREPARED
                    }
                    if (notStartedService != null) {
                        try {
                            notStartedService.start()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (task.minServiceCount > taskServices.size && task.isCurrentLessLoadedNode()) {
                        serviceManager.runTask(task)
                    }
                }
            }
        }
    }

    suspend fun stopDeadServices() {

    }

    fun ServiceTask.isCurrentLessLoadedNode(): Boolean {
        val nodeServers = NodeManager.getNodeServers(this)
        var allow = true
        for (nodeServer in nodeServers) {
            val freeMemory = nodeServer.nodeInfo.maxMemory - nodeServer.nodeInfo.reservedMemory
            val avgCpuUsage = nodeServer.nodeInfo.processSnapshot.cpuUsage * nodeServer.nodeInfo.currentServicesCount
            val currentFreeMemory = currentNetworkNodeInfo.maxMemory - currentNetworkNodeInfo.reservedMemory
            val currentAvgCpuUsage =
                currentNetworkNodeInfo.processSnapshot.cpuUsage * currentNetworkNodeInfo.currentServicesCount
            if (freeMemory > currentFreeMemory && avgCpuUsage < currentAvgCpuUsage) {
                allow = false
            }
        }
        return nodeServers.isEmpty() || allow
    }

    fun ServiceTask.searchLessLoadedNode(): NetworkNodeInfo {
        val nodeServers = NodeManager.getNodeServers(this).map { it.nodeInfo }
        val windows = nodeServers.any { it.systemCpuUsage == -1.0 } // on windows systemCpuUsage always -1
        return if (windows) {
            nodeServers.maxBy { it.maxMemory - it.reservedMemory }
        } else {
            nodeServers.maxBy { (it.maxMemory - it.reservedMemory) + (100 - it.systemCpuUsage) }
        } ?: currentNetworkNodeInfo
    }

    fun sendNodeUpdate() {
        lastNetworkNodeInfo = currentNetworkNodeInfo
        currentNetworkNodeInfo = createNetworkNodeInfo()
    }

    fun createNetworkNodeInfo(): NetworkNodeInfo = NetworkNodeInfo(
        Instant.now(),
        nodeConfiguration.identity,
        javaClass.`package`.implementationVersion,
        serviceManager.localServices.size,
        serviceManager.currentUsedHeapMemory,
        serviceManager.currentReservedMemory,
        nodeConfiguration.maxMemory,
        ProcessSnapshot(),
        SystemStatistics.systemLoadAverage
    )
}

