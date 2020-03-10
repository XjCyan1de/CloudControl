package com.github.xjcyan1de.cloudcontrol

import com.github.xjcyan1de.cloudcontrol.api.*
import com.github.xjcyan1de.cloudcontrol.api.console.Console
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNodeConfiguration
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNodeProvider
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNodeSnapshot
import com.github.xjcyan1de.cloudcontrol.api.network.findLessLoaded
import com.github.xjcyan1de.cloudcontrol.api.service.*
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ConfigurationManager
import com.github.xjcyan1de.cloudcontrol.api.util.SystemStatistics
import com.github.xjcyan1de.cloudcontrol.network.NodeServerProvider
import com.github.xjcyan1de.cloudcontrol.network.nodeServers
import com.github.xjcyan1de.cloudcontrol.service.CloudServiceManager
import com.github.xjcyan1de.cloudcontrol.service.NodeCloudServiceWrapper
import com.github.xjcyan1de.cyanlibz.messenger.Messenger
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

object CloudControlNode : CloudControlDriver {
    val processQueue = ConcurrentLinkedQueue<() -> Unit>()
    override val console: Console
        get() = TODO("Not yet implemented")
    override val generalCloudServiceProvider = CloudServiceManager
    override val serviceTaskProvider = CloudServiceManager
    override val networkNodeProvider: NetworkNodeProvider = NodeServerProvider
    override lateinit var networkNodeConfiguration: NetworkNodeConfiguration
    override lateinit var messenger: Messenger
    override lateinit var currentNetworkNodeSnapshot: NetworkNodeSnapshot
    override lateinit var lastNetworkNodeSnapshot: NetworkNodeSnapshot

    override fun start() {
        Runtime.getRuntime().addShutdownHook(thread(start = false, name = "Shutdown Thread") { stop() })

        networkNodeConfiguration = NetworkNodeConfiguration.load()
        currentNetworkNodeSnapshot = createNetworkNodeSnapshot()
        lastNetworkNodeSnapshot = currentNetworkNodeSnapshot

        ConfigurationManager.load()
        NodeServerProvider.setClusterServers(networkNodeConfiguration.cluster)

        val tempDirectory = TEMP_DIR.toFile()
        tempDirectory.mkdirs()
        val localDirectory = LOCAL_DIR.toFile()
        localDirectory.mkdirs()

        File(tempDirectory, "caches").mkdir()

        mainLoop()
    }

    override fun stop() {
        //
    }

    override fun getCloudServiceWrapper(name: String): CloudServiceWrapper? {
        val cloudService = runBlocking { getCloudService(name) }
        return cloudService?.toWrapper()
    }

    override fun getCloudServiceWrapper(uniqueId: UUID): CloudServiceWrapper? {
        val cloudService = runBlocking { getCloudService(uniqueId) }
        return cloudService?.toWrapper()
    }

    override fun getCloudServiceWrapper(serviceInfoSnapshot: ServiceInfoSnapshot): CloudServiceWrapper =
        NodeCloudServiceWrapper(serviceInfoSnapshot)


    fun runConsole() {
        TODO()
    }

    /**
     * Главный цикл приложения, который выполняется 100 раз в секунду
     */
    fun mainLoop() = runBlocking {
        var value = System.currentTimeMillis()
        val millis = 1000 / TPS.toLong()
        var launchServicesTimer = TPS

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

                invokeProcessQueue()

                if (launchServicesTimer++ >= TPS) {
                    launchServices()
                    launchServicesTimer = 0
                }

                value = System.currentTimeMillis()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun invokeProcessQueue() {
        while (!processQueue.isEmpty()) {
            if (processQueue.peek() != null) {
                processQueue.poll().invoke()
            } else {
                processQueue.poll()
            }
        }
    }

    suspend fun launchServices() {
        for (task in serviceTasks) {
            try {
                launchServices(task)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun launchServices(task: ServiceTask) {
        if (task.canStartServices()) {
            val nodes = task.associatedNodes
            val minServiceCount = task.minServiceCount
            val taskServices = task.cloudServices
            val runningServices = taskServices.filter { it.lifeCycle == ServiceLifeCycle.RUNNING }
            val currentNode = networkNodeConfiguration.identity

            if (nodes.isEmpty() || (nodes.contains(currentNode) && minServiceCount > runningServices.size)) {
                val localServices = CloudServiceManager.getLocalCloudServices(task)
                val notStartedService = localServices.find {
                    it.lifeCycle == ServiceLifeCycle.DEFINED || it.lifeCycle == ServiceLifeCycle.PREPARED
                }

                if (notStartedService != null) {
                    notStartedService.start()
                } else {
                    val taskNodeServers = task.nodeServers
                    val lessLoaded = taskNodeServers.map { it.nodeSnapshot }.findLessLoaded()
                    val isCurrentNodeLessLoaded = currentNetworkNodeSnapshot == lessLoaded

                    if (minServiceCount > taskServices.size && isCurrentNodeLessLoaded) {
                        val cloudService = CloudServiceManager.runTask(task)
                        cloudService.start()
                    }
                }
            }
        }
    }

    suspend fun stopDeadServices() {
        TODO()
    }

    fun sendNodeUpdate() {
        lastNetworkNodeSnapshot = currentNetworkNodeSnapshot
        currentNetworkNodeSnapshot = createNetworkNodeSnapshot()
    }

    fun sendServiceUpdate(serviceInfoSnapshot: ServiceInfoSnapshot) {
        //TODO
    }

    fun createNetworkNodeSnapshot(): NetworkNodeSnapshot {
        val cloudServices = cloudServices
        return NetworkNodeSnapshot(
            Instant.now(),
            networkNodeConfiguration.identity,
            VERSION,
            cloudServices.size,
            cloudServices.currentUsedHeapMemory,
            cloudServices.currentReservedMemory,
            networkNodeConfiguration.maxMemory,
            ProcessSnapshot(),
            SystemStatistics.systemLoadAverage
        )
    }
}

