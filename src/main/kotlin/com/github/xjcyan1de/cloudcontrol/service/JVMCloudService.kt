package com.github.xjcyan1de.cloudcontrol.service

import com.github.xjcyan1de.cloudcontrol.CloudControlNode
import com.github.xjcyan1de.cloudcontrol.api.PRESISTANCE_SERVICES_DIR
import com.github.xjcyan1de.cloudcontrol.api.SERVICE_ERROR_RESTART_DELAY
import com.github.xjcyan1de.cloudcontrol.api.TEMP_DIR
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkAddress
import com.github.xjcyan1de.cloudcontrol.api.service.*
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import com.github.xjcyan1de.cloudcontrol.api.util.SystemStatistics
import com.github.xjcyan1de.cloudcontrol.service.configuration.BungeeConfigurator
import com.github.xjcyan1de.cloudcontrol.service.configuration.NMSConfigurator
import com.github.xjcyan1de.cloudcontrol.template.getStorage
import com.github.xjcyan1de.cyanlibz.localization.textOf
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Files
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import java.util.jar.JarFile
import kotlin.collections.ArrayList
import kotlin.random.Random

class JVMCloudService(
    val serviceConfiguration: ServiceConfiguration
) {
    val templates: List<ServiceTemplate> = ArrayList()
    val deployments: MutableList<ServiceDeployment> = ArrayList()
    val waitingTemplates: Queue<ServiceTemplate> = ConcurrentLinkedQueue()
    val groups: List<ServiceGroup> = ArrayList()
    var lifeCycle: ServiceLifeCycle = ServiceLifeCycle.DEFINED
    val serviceId: ServiceId = serviceConfiguration.serviceId
    val connectionKey: String = Base64.getEncoder().encodeToString(Random.nextBytes(256))
    var serviceInfoSnapshot: ServiceInfoSnapshot = ServiceInfoSnapshot(serviceConfiguration, lifeCycle)
    val lastServiceInfoSnapshot: ServiceInfoSnapshot = serviceInfoSnapshot
    var process: Process? = null
    val configuredMaxHeapMemory: Int = serviceConfiguration.processConfiguration.maxHeapMemorySize

    @Volatile
    private var restartState: Boolean = false

    private val directory = if (serviceConfiguration.staticService) {
        File(PRESISTANCE_SERVICES_DIR.toFile(), serviceId.name)
    } else {
        File(TEMP_DIR.toFile(), serviceId.name + "_" + serviceId.uniqueId.toString())
    }
    private val firstStartupOnStaticService = serviceConfiguration.staticService && !directory.exists()

    init {
        directory.mkdirs()
        initializeAndPrepare()
    }

    fun initializeAndPrepare() {
        if (lifeCycle == ServiceLifeCycle.DEFINED || lifeCycle == ServiceLifeCycle.STOPPED) {
            log("cloud_service.pre_prepared")

            lifeCycle = ServiceLifeCycle.PREPARED
            serviceInfoSnapshot.lifeCycle = ServiceLifeCycle.PREPARED

            CloudControlNode.sendServiceUpdate(serviceInfoSnapshot)

            log("cloud_service.post_prepared")
        }
    }

    fun runCommand(commandLine: String) {
        val process = process

        if (lifeCycle == ServiceLifeCycle.RUNNING && process != null && process.isAlive) {
            try {
                val outputStream = process.outputStream

                outputStream.write("$commandLine\n".toByteArray())
                outputStream.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun start() {
        includeTemplates()

        serviceInfoSnapshot = createServiceInfoSnapshot(ServiceLifeCycle.PREPARED)
        CloudServiceManager.serviceInfoSnapshotsMap[serviceInfoSnapshot.serviceId.uniqueId] = serviceInfoSnapshot

        if (!CloudControlNode.networkNodeConfiguration.parallelServiceStartSequence) {
            synchronized(this) {
                startProcess()
            }
        } else {
            startProcess()
        }
    }

    @Synchronized
    fun restart() {
        restartState = true

        stop()
        start()

        restartState = false
    }

    @Synchronized
    fun stop(): Int = stop0(false)

    @Synchronized
    fun kill(): Int = stop0(true)

    private fun invokeAutoDeleteOnStopIfNotRestart() {
        if (serviceConfiguration.autoDeleteOnStop && !restartState) {
            delete()
        } else {
            initializeAndPrepare()
        }
    }

    @Synchronized
    fun delete() {
        if (lifeCycle == ServiceLifeCycle.DELETED) {
            return
        }

        if (lifeCycle == ServiceLifeCycle.RUNNING) {
            stop0(true)
        }

        delete0()

        serviceInfoSnapshot.lifeCycle = ServiceLifeCycle.DELETED
        CloudControlNode.sendServiceUpdate(serviceInfoSnapshot)
    }

    fun isAlive(): Boolean = lifeCycle == ServiceLifeCycle.DEFINED || lifeCycle == ServiceLifeCycle.PREPARED ||
            (lifeCycle == ServiceLifeCycle.RUNNING && process?.isAlive == true)

    fun includeTemplates() {
        while (!waitingTemplates.isEmpty()) {
            val template = waitingTemplates.poll()
            val storage = getStorage(template.storage)

            if (!storage.has(template)) {
                continue
            }

            try {
                if (!serviceConfiguration.staticService || template.isShouldAlwaysCopyToStaticServices || firstStartupOnStaticService) {
                    log("cloud_service.include_template")
                    storage.copy(template, directory)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deployResources(removeDeployments: Boolean = true) {
        for (deployment in deployments) {
            val storage = getStorage(deployment.template.storage)

            log("cloud_service.deploy")

            storage.deploy(directory, deployment.template) { file: File ->
                val isWrapperFile = file.name == "wrapper.jar" || file.name == ".wrapper"
                val fileName = if (file.isDirectory) {
                    file.name + "/"
                } else {
                    file.name
                }

                !isWrapperFile && !deployment.excludes.contains(fileName)
            }

            if (removeDeployments) {
                deployments.remove(deployment)
            }
        }
    }

    private fun startProcess() {
        if (lifeCycle == ServiceLifeCycle.PREPARED || lifeCycle == ServiceLifeCycle.STOPPED) {
            val maxHeapMemory = CloudControlNode.networkNodeConfiguration.maxMemory
            val maxCPUUsage = CloudControlNode.networkNodeConfiguration.maxCPUUsageToStartServices

            if (!SystemStatistics.isPermissibleSystemLoad(configuredMaxHeapMemory, maxHeapMemory, maxCPUUsage)) {
                return
            }

            startPrepared0()
            start0()

            serviceInfoSnapshot.lifeCycle = lifeCycle

            CloudControlNode.sendServiceUpdate(serviceInfoSnapshot)
        }
    }

    private fun startPrepared0() {
        val serviceUniqueId = serviceInfoSnapshot.serviceId.uniqueId

        log("cloud_service.pre_start_prepared")

        includeTemplates()

        serviceInfoSnapshot = createServiceInfoSnapshot(ServiceLifeCycle.PREPARED)
        CloudServiceManager.serviceInfoSnapshotsMap[serviceUniqueId] = serviceInfoSnapshot

        log("cloud_service.post_start_prepared")
    }

    private fun start0() {
        log("cloud_service.pre_start")

        configureServiceEnvironment()
        if (!startWrapper()) {
            return
        }

        lifeCycle = ServiceLifeCycle.RUNNING

        log("cloud_service.post_start")
    }

    private fun stop0(force: Boolean): Int {
        log("cloud_service.pre_stop")

        val exitValue = stopProcess(force)

        lifeCycle = ServiceLifeCycle.STOPPED

        for (path in serviceConfiguration.deletedFilesAfterStop) {
            val file = File(directory, path)

            if (file.exists()) {
                file.deleteRecursively()
            }
        }

        log("cloud_service.post_stop", "exit_value" to exitValue)

        serviceInfoSnapshot = createServiceInfoSnapshot(ServiceLifeCycle.STOPPED)

        CloudControlNode.sendServiceUpdate(serviceInfoSnapshot)
        invokeAutoDeleteOnStopIfNotRestart()

        return exitValue
    }

    private fun stopProcess(force: Boolean): Int {
        val process = process

        if (process != null) {
            try {
                runCommand("stop")
                runCommand("end")

                if (process.waitFor(5, TimeUnit.SECONDS)) {
                    return process.exitValue()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (!force) {
                process.destroy()
            } else {
                process.destroyForcibly()
            }

            try {
                process.exitValue()
            } catch (ignored: Throwable) {
                if (!force) {
                    stopProcess(true)
                }

                return -1
            }
        }

        return -1
    }

    private fun delete0() {
        val serviceUniqueId = serviceId.uniqueId

        log("cloud_service.pre_delete")

        deployResources()

        if (!serviceConfiguration.staticService) {
            directory.deleteRecursively()
        }

        lifeCycle = ServiceLifeCycle.DELETED

        CloudServiceManager.serviceInfoSnapshotsMap.remove(serviceUniqueId)
        CloudServiceManager.cloudServicesMap.remove(serviceUniqueId)

        log("cloud_service.post_delete")
    }

    private fun configureServiceEnvironment() {
        when (serviceConfiguration.processConfiguration.environment) {
            ServiceEnvironment.BUNGEECORD, ServiceEnvironment.WATERFALL -> {
                val file = File(directory, "config.yml").copyDefaultFile("files/bungee/config.yml")

                BungeeConfigurator(serviceConfiguration).rewrite(file)
            }
            ServiceEnvironment.PAPER -> {
                val serverProperties =
                    File(directory, "server.properties").copyDefaultFile("files/nms/server.properties")
                val eulaTxt = File(directory, "eula.txt").copyDefaultFile()
                val configurator = NMSConfigurator(serviceConfiguration)

                configurator.rewrite(serverProperties)
                configurator.rewrite(eulaTxt)
            }
            else -> TODO()
        }
    }

    private fun File.copyDefaultFile(path: String? = null) = apply {
        if (!this.exists() && this.createNewFile() && path != null) {
            javaClass.getResourceAsStream(path)?.use {
                Files.copy(it, toPath())
            }
        }
    }

    private fun startWrapper(): Boolean {
        val applicationJar = directory.listFiles()!!.asSequence()
            .filter { it.name.endsWith(".jar") }
            .filter { it.name.contains(serviceConfiguration.processConfiguration.environment.name, true) }
            .firstOrNull()

        if (applicationJar == null) {
            log("cloud_service.jar_file_not_found_error")
            val serviceTask = runBlocking { CloudServiceManager.getServiceTask(serviceId.task.name) }
            serviceTask?.forbidServiceStarting(SERVICE_ERROR_RESTART_DELAY * 1000)
            stop()

            return false
        }

        val commandArguments = LinkedList<String>()

        commandArguments.add(CloudControlNode.networkNodeConfiguration.jvmCommand)
        commandArguments.addAll(CloudControlNode.networkNodeConfiguration.defaultJVMOptionParameters)
        commandArguments.addAll(serviceConfiguration.processConfiguration.jvmOptions)
        commandArguments.add("-Xmx$configuredMaxHeapMemory")
        commandArguments.add("-Xms$configuredMaxHeapMemory")
        commandArguments.add("-cp")

        commandArguments.add(applicationJar.absolutePath)

        JarFile(applicationJar).use {
            commandArguments.add(it.manifest.mainAttributes["Main-Class"].toString())
        }

        commandArguments.add("nogui")
        println(commandArguments.joinToString(" "))

        process = ProcessBuilder().command(commandArguments).directory(directory).start()

        return true
    }

    private fun log(key: String, vararg replaces: Pair<String, Any>) {
        println(
            textOf(
                key,
                "task" to { serviceId.task.name },
                "service_id" to { serviceId.taskServiceId },
                "id" to { serviceId.uniqueId },
                *replaces.map { it.first to { it.second } }.toTypedArray()
            )
        )
    }

    private fun createServiceInfoSnapshot(lifeCycle: ServiceLifeCycle): ServiceInfoSnapshot =
        ServiceInfoSnapshot(
            Instant.now(),
            serviceId,
            NetworkAddress(CloudControlNode.networkNodeConfiguration.hostAddress, serviceConfiguration.port),
            false,
            lifeCycle,
            ProcessSnapshot.empty(),
            serviceConfiguration.copy()
        )
}