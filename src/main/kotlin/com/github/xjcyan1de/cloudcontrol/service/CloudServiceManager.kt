package com.github.xjcyan1de.cloudcontrol.service

import com.github.xjcyan1de.cloudcontrol.CloudControlNode
import com.github.xjcyan1de.cloudcontrol.api.service.*
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ConfigurationManager
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.toServiceConfiguration
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object CloudServiceManager : CloudServiceFactory, GeneralCloudServiceProvider, ServiceTaskProvider {
    val serviceInfoSnapshotsMap = ConcurrentHashMap<UUID, ServiceInfoSnapshot>()
    val cloudServicesMap = ConcurrentHashMap<UUID, CloudService>()
    val localServicesMap = ConcurrentHashMap<UUID, CloudService>()

    override suspend fun createCloudService(serviceTask: ServiceTask): ServiceInfoSnapshot {
        val taskIds = serviceTask.getReservedTaskIds()
        var taskId = 1

        while (taskIds.contains(taskId)) {
            taskId++
        }

        val serviceConfiguration = serviceTask.toServiceConfiguration(
            ServiceId(
                UUID.randomUUID(),
                CloudControlNode.networkNodeConfiguration.identity.name,
                serviceTask,
                taskId,
                serviceTask.processConfiguration.environment
            )
        )
        return createCloudService(serviceConfiguration)
    }

    override suspend fun createCloudService(serviceConfiguration: ServiceConfiguration): ServiceInfoSnapshot {
        return CloudService(serviceConfiguration).serviceInfoSnapshot
    }

    override suspend fun getServicesUniqueIds(): Collection<UUID> =
        serviceInfoSnapshotsMap.keys

    override suspend fun getCloudServices(): Collection<ServiceInfoSnapshot> =
        serviceInfoSnapshotsMap.values

    override suspend fun getCloudServices(serviceTask: ServiceTask): Collection<ServiceInfoSnapshot> =
        getCloudServices().filter { it.serviceId.task == serviceTask }

    override suspend fun getCloudServices(serviceEnvironment: ServiceEnvironment): Collection<ServiceInfoSnapshot> =
        getCloudServices().filter { it.serviceId.environment == serviceEnvironment }

    override suspend fun getCloudServices(serviceGroup: ServiceGroup): Collection<ServiceInfoSnapshot> =
        getCloudServices().filter {
            it.serviceConfiguration.groups.contains(
                serviceGroup.name
            )
        }

    override suspend fun getStartedCloudServices(): Collection<ServiceInfoSnapshot> =
        getCloudServices().filter { it.lifeCycle == ServiceLifeCycle.RUNNING }

    override suspend fun getServicesCount(): Int = getCloudServices().size

    override suspend fun getServicesCount(serviceGroup: ServiceGroup): Int =
        getCloudServices().count {
            it.serviceConfiguration.groups.contains(
                serviceGroup.name
            )
        }

    override suspend fun getServicesCount(serviceTask: ServiceTask): Int =
        getCloudServices().count { it.serviceId.task == serviceTask }

    override suspend fun getServicesCount(serviceEnvironment: ServiceEnvironment): Int =
        getCloudServices().count { it.serviceId.environment == serviceEnvironment }

    override suspend fun getCloudService(name: String): ServiceInfoSnapshot? =
        getCloudServices().find { it.serviceId.name.equals(name, true) }

    override suspend fun getCloudService(uniqueId: UUID): ServiceInfoSnapshot? =
        serviceInfoSnapshotsMap[uniqueId]

    override suspend fun getServiceTasks(): Collection<ServiceTask> = ConfigurationManager.tasks

    override suspend fun getServiceTask(name: String): ServiceTask? =
        getServiceTasks().find { it.name.equals(name, true) }

    override suspend fun isServiceTaskPresent(name: String): Boolean = getServiceTask(name) != null

    override suspend fun addPermanentServiceTask(serviceTask: ServiceTask) {
        TODO("Not yet implemented")
    }

    override suspend fun removePermanentServiceTask(serviceTask: ServiceTask) {
        TODO("Not yet implemented")
    }

    fun getLocalCloudServices(): Collection<CloudService> = localServicesMap.values

    fun getLocalCloudServices(serviceTask: ServiceTask): Collection<CloudService> = localServicesMap.values.filter {
        it.id.task == serviceTask
    }

    suspend fun runTask(serviceTask: ServiceTask): CloudService {
        TODO()
    }

    suspend fun runTask(serviceConfiguration: ServiceConfiguration): CloudService {
        TODO()
    }
}