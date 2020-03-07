package com.github.xjcyan1de.cloudcontrol.service

import com.github.xjcyan1de.cloudcontrol.CloudControlNode
import com.github.xjcyan1de.cloudcontrol.api.service.*
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ConfigurationManager
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import com.github.xjcyan1de.cloudcontrol.api.util.PortResolver
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object CloudServiceManager : GeneralCloudServiceProvider, ServiceTaskProvider {
    val serviceInfoSnapshotsMap = ConcurrentHashMap<UUID, ServiceInfoSnapshot>()
    val cloudServicesMap = ConcurrentHashMap<UUID, JVMCloudService>()
    val localServicesMap = ConcurrentHashMap<UUID, JVMCloudService>()

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

    fun getLocalCloudServices(): Collection<JVMCloudService> = localServicesMap.values

    fun getLocalCloudServices(serviceTask: ServiceTask): Collection<JVMCloudService> = localServicesMap.values.filter {
        it.serviceId.task == serviceTask
    }

    fun runTask(serviceTask: ServiceTask): JVMCloudService {
        var taskId = 1
        val reservedTaskIds = serviceTask.getReservedTaskIds()

        while (reservedTaskIds.contains(taskId)) {
            taskId++
        }

        val templates = ArrayList<ServiceTemplate>().apply { addAll(serviceTask.templates) }
        val deployments = ArrayList<ServiceDeployment>().apply { addAll(serviceTask.deployments) }
        val properties = HashMap<String, String>()

        for (group in ConfigurationManager.groups) {
            if (serviceTask.groups.contains(group.name)) {
                templates.addAll(group.templates)
                deployments.addAll(group.deployments)
                properties.putAll(group.properties)
            }
        }

        val serviceConfiguration = ServiceConfiguration(
            ServiceId(
                UUID.randomUUID(),
                CloudControlNode.currentNetworkNodeSnapshot.node.name,
                serviceTask,
                taskId,
                serviceTask.processConfiguration.environment
            ),
            serviceTask.autoDeleteOnStop,
            serviceTask.staticServices,
            serviceTask.startPort,
            serviceTask.groups,
            emptyList(),
            templates, deployments,
            properties
        )
        return runTask(serviceConfiguration)
    }

    @Suppress("NAME_SHADOWING")
    fun runTask(serviceConfiguration: ServiceConfiguration): JVMCloudService {
        val port = PortResolver.resolvePort(serviceConfiguration.port)
        val serviceConfiguration = if (port == serviceConfiguration.port) {
            serviceConfiguration
        } else {
            serviceConfiguration.copy(port = port)
        }
        val cloudService = JVMCloudService(serviceConfiguration)

        cloudServicesMap[cloudService.serviceId.uniqueId] = cloudService
        serviceInfoSnapshotsMap[cloudService.serviceId.uniqueId] = cloudService.serviceInfoSnapshot
        CloudControlNode.sendNodeUpdate()

        return cloudService
    }
}