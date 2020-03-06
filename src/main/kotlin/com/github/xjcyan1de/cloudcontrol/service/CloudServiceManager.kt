package com.github.xjcyan1de.cloudcontrol.service

import com.github.xjcyan1de.cloudcontrol.CloudControlNode
import com.github.xjcyan1de.cloudcontrol.api.service.*
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ConfigurationManager
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.toServiceConfiguration
import java.util.*
import java.util.concurrent.ConcurrentHashMap

interface CloudServiceManager {
    val serviceTasks: List<ServiceTask>
    val serviceInfoSnapshots: Collection<ServiceInfoSnapshot>
    val localServices: Collection<CloudService>

    fun getServiceInfoSnapshot(uniqueId: UUID): ServiceInfoSnapshot?

    fun getServiceInfoSnapshot(predicate: (ServiceInfoSnapshot) -> Boolean): ServiceInfoSnapshot? =
        serviceInfoSnapshots.find(predicate)

    fun getServiceInfoSnapshot(name: String): ServiceInfoSnapshot? =
        getServiceInfoSnapshot { it.serviceId.name.equals(name, true) }

    fun getServiceInfoSnapshots(predicate: (ServiceInfoSnapshot) -> Boolean): Collection<ServiceInfoSnapshot> =
        serviceInfoSnapshots.filter(predicate)

    fun getServiceInfoSnapshots(task: ServiceTask): Collection<ServiceInfoSnapshot> = getServiceInfoSnapshots {
        it.serviceId.task == task
    }

    fun getServiceInfoSnapshots(environment: ServiceEnvironment): Collection<ServiceInfoSnapshot> =
        getServiceInfoSnapshots {
            it.serviceId.environment == environment
        }

    fun getLocalCloudServices(predicate: (CloudService) -> Boolean): Collection<CloudService> =
        localServices.filter(predicate)

    fun getLocalCloudServices(task: ServiceTask): Collection<CloudService> = getLocalCloudServices {
        it.id.task == task
    }

    fun getLocalCloudServices(environment: ServiceEnvironment): Collection<CloudService> = getLocalCloudServices {
        it.id.environment == environment
    }

    fun getReservedTaskIds(task: ServiceTask): Collection<Int> =
        serviceInfoSnapshots.filter { it.serviceId.task == task }.map { it.serviceId.taskServiceId }

    fun runTask(task: ServiceTask) {
        val taskIds = getReservedTaskIds(task)
        var taskId = 1
        while (taskIds.contains(taskId)) {
            taskId++
        }
        val serviceConfiguration = task.toServiceConfiguration(
            ServiceId(
                UUID.randomUUID(),
                CloudControlNode.nodeConfiguration.identity.name,
                task,
                taskId,
                task.processConfiguration.environment
            )
        )
        runTask(serviceConfiguration)
    }

    fun runTask(serviceConfiguration: ServiceConfiguration)

    val currentUsedHeapMemory: Int
        get() =
            localServices.filter { it.lifeCycle == ServiceLifeCycle.RUNNING }.map { it.configuredMaxHeapMemory }.sum()
    val currentReservedMemory: Int get() = localServices.map { it.configuredMaxHeapMemory }.sum()
}

fun CloudServiceManager(): CloudServiceManager = CloudServiceManagerImpl()

internal class CloudServiceManagerImpl : CloudServiceManager {
    private val serviceInfoSnapshotsMap = ConcurrentHashMap<UUID, ServiceInfoSnapshot>()
    private val cloudServicesMap = ConcurrentHashMap<UUID, CloudService>()
    private val localServicesMap = ConcurrentHashMap<UUID, CloudService>()
    override val serviceTasks: List<ServiceTask> get() = ConfigurationManager.tasks
    override val serviceInfoSnapshots get() = serviceInfoSnapshotsMap.values
    override val localServices get() = cloudServicesMap.values

    override fun getServiceInfoSnapshot(uniqueId: UUID): ServiceInfoSnapshot? = serviceInfoSnapshotsMap[uniqueId]

    override fun runTask(serviceConfiguration: ServiceConfiguration) {
        println("Run task: $serviceConfiguration")
    }
}