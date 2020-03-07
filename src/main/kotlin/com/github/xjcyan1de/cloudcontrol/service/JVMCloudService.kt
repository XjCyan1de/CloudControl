package com.github.xjcyan1de.cloudcontrol.service

import com.github.xjcyan1de.cloudcontrol.CloudControlNode
import com.github.xjcyan1de.cloudcontrol.api.PRESISTANCE_SERVICES_DIR
import com.github.xjcyan1de.cloudcontrol.api.TEMP_DIR
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkAddress
import com.github.xjcyan1de.cloudcontrol.api.service.*
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import com.github.xjcyan1de.cloudcontrol.api.util.SystemStatistics
import com.github.xjcyan1de.cloudcontrol.template.getStorage
import com.github.xjcyan1de.cyanlibz.localization.textOf
import java.io.File
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.random.Random

class JVMCloudService(
    val serviceConfiguration: ServiceConfiguration
) {
    val templates: List<ServiceTemplate> = ArrayList()
    val deployments: List<ServiceDeployment> = ArrayList()
    val waitingTemplates: Queue<ServiceTemplate> = ConcurrentLinkedQueue()
    val groups: List<ServiceGroup> = ArrayList()
    var lifeCycle: ServiceLifeCycle = ServiceLifeCycle.DEFINED
    val serviceId: ServiceId = serviceConfiguration.serviceId
    val connectionKey: String = Base64.getEncoder().encodeToString(Random.nextBytes(256))
    var serviceInfoSnapshot: ServiceInfoSnapshot = ServiceInfoSnapshot(serviceConfiguration, lifeCycle)
    val lastServiceInfoSnapshot: ServiceInfoSnapshot = serviceInfoSnapshot
    val process: Process? = null
    val configuredMaxHeapMemory: Int = serviceConfiguration.processConfiguration.maxHeapMemorySize

    private val lifeCycleLock: Lock = ReentrantLock()
    private val directory = if (serviceConfiguration.staticService) {
        File(PRESISTANCE_SERVICES_DIR.toFile(), serviceId.name)
    } else {
        File(TEMP_DIR.toFile(), serviceId.name + "_" + serviceId.uniqueId.toString())
    }
    private val firstStartupOnStaticService = serviceConfiguration.staticService && !directory.exists()

    init {
        directory.mkdirs()
        if (lifeCycle == ServiceLifeCycle.DEFINED || lifeCycle == ServiceLifeCycle.STOPPED) {
            println(textOf("cloud_service.pre_prepared",
                "task" to { serviceId.task.name },
                "service_id" to { serviceId.taskServiceId },
                "id" to { serviceId.uniqueId }
            ).get())
            lifeCycle = ServiceLifeCycle.PREPARED
            serviceInfoSnapshot.lifeCycle = ServiceLifeCycle.PREPARED
            CloudControlNode.sendServiceUpdate(serviceInfoSnapshot)
            println(textOf("cloud_service.post_prepared",
                "task" to { serviceId.task.name },
                "service_id" to { serviceId.taskServiceId },
                "id" to { serviceId.uniqueId }
            ).get())
        }
    }

    fun runCommand(commandLine: String) {
        TODO("Not yet implemented")
    }

    fun start() {
        println("Starting service: $serviceConfiguration")

        includeTemplates()

        serviceInfoSnapshot = createServiceInfoSnapshot(ServiceLifeCycle.PREPARED)
        CloudServiceManager.serviceInfoSnapshotsMap[serviceInfoSnapshot.serviceId.uniqueId] = serviceInfoSnapshot

        println(textOf("cloud_service.post_start_prepared",
            "task" to { serviceId.task.name },
            "service_id" to { serviceId.taskServiceId },
            "id" to { serviceId.uniqueId }
        ).get())
        println(textOf("cloud_service.pre_start",
            "task" to { serviceId.task.name },
            "service_id" to { serviceId.taskServiceId },
            "id" to { serviceId.uniqueId }
        ).get())


    }

    fun restart() {
        TODO("Not yet implemented")
    }

    fun stop() {
        TODO("Not yet implemented")
    }

    fun kill() {
        TODO("Not yet implemented")
    }

    fun delete() {
        TODO("Not yet implemented")
    }

    fun isAlive(): Boolean {
        TODO("Not yet implemented")
    }

    fun includeTemplates() {
        while (!waitingTemplates.isEmpty()) {
            val template = waitingTemplates.poll()
            val storage = getStorage(template.storage)

            if (!storage.has(template)) {
                continue
            }

            try {
                if (!serviceConfiguration.staticService || template.isShouldAlwaysCopyToStaticServices || firstStartupOnStaticService) {
                    println(textOf("cloud_service.include_template",
                        "task" to { serviceId.task.name },
                        "id" to { serviceId.uniqueId },
                        "service_id" to { serviceId.taskServiceId },
                        "template" to { template.templatePath },
                        "storage" to { template.storage }
                    ))
                    storage.copy(template, directory)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deployResources(removeDeployments: Boolean) {
        TODO("Not yet implemented")
    }

    private fun startProcess() {
        if (lifeCycle == ServiceLifeCycle.PREPARED || lifeCycle == ServiceLifeCycle.STOPPED) {
            if (!isPermissibleSystemLoad()) {
                return
            }
            println(textOf("cloud_service.pre_start_prepared",
                "task" to { serviceId.task.name },
                "service_id" to { serviceId.task.name },
                "id" to { serviceId.uniqueId }
            ).get())

            includeTemplates()

            serviceInfoSnapshot = createServiceInfoSnapshot(ServiceLifeCycle.PREPARED)
            CloudServiceManager.serviceInfoSnapshotsMap[serviceInfoSnapshot.serviceId.uniqueId] = serviceInfoSnapshot

            println(textOf("cloud_service.post_start_prepared",
                "task" to { serviceId.task.name },
                "service_id" to { serviceId.task.name },
                "id" to { serviceId.uniqueId }
            ).get())
            println(textOf("cloud_service.pre_start",
                "task" to { serviceId.task.name },
                "service_id" to { serviceId.task.name },
                "id" to { serviceId.uniqueId }
            ).get())

        }
        TODO()
    }

    private fun configureServiceEnvironment() {

        TODO()
    }

    @Throws(java.lang.Exception::class)
    private fun rewriteServiceConfigurationFile(
        file: File,
        block: (String) -> String
    ) {
        val lines = file.readLines()

        lines.forEach {

        }
    }

    private fun isPermissibleSystemLoad(): Boolean {
        val expectedMemoryUsage = cloudServices.currentUsedHeapMemory + configuredMaxHeapMemory
        if (expectedMemoryUsage >= CloudControlNode.networkNodeConfiguration.maxMemory) {
            println(textOf("cloud_service.manager.max_memory_error").get())
            return false
        }
        if (SystemStatistics.systemCPUusage >= CloudControlNode.networkNodeConfiguration.maxCPUUsageToStartServices) {
            println(textOf("cloud_service.manager.cpu_usage_to_high_error").get())
            return false
        }
        return true
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