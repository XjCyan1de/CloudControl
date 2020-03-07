package com.github.xjcyan1de.cloudcontrol.api.service

import com.github.xjcyan1de.cloudcontrol.api.CloudControl
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNode
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ProcessConfiguration
import kotlinx.coroutines.runBlocking

data class ServiceTask(
    val name: String,
    val runtime: String,
    val maintenance: Boolean,
    val static: Boolean,
    val associatedNodes: List<NetworkNode>,
    val groups: List<String>,
    val processConfiguration: ProcessConfiguration,
    val startPort: Int,
    val minServiceCount: Int,
    val templates: List<ServiceTemplate>,
    val deployments: List<ServiceDeployment>
) {
    private var serviceStartAbilityTime = -1L

    fun forbidServiceStarting(time: Long) {
        serviceStartAbilityTime = System.currentTimeMillis() + time
    }

    fun canStartServices(): Boolean = !maintenance && System.currentTimeMillis() >= serviceStartAbilityTime

    override fun equals(other: Any?): Boolean = name.equals((other as? ServiceTask)?.name, true)

    override fun hashCode(): Int = name.hashCode()
}

interface ServiceTaskProvider {
    suspend fun getServiceTasks(): Collection<ServiceTask>
    suspend fun getServiceTask(name: String): ServiceTask?
    suspend fun isServiceTaskPresent(name: String): Boolean
    suspend fun addPermanentServiceTask(serviceTask: ServiceTask)
    suspend fun removePermanentServiceTask(serviceTask: ServiceTask)

    companion object
}

val serviceTasks: Collection<ServiceTask>
    get() = runBlocking { CloudControl.serviceTaskProvider.getServiceTasks() }

suspend fun getServiceTask(name: String): ServiceTask? =
    CloudControl.serviceTaskProvider.getServiceTask(name)

suspend fun isServiceTaskPresent(name: String): Boolean =
    CloudControl.serviceTaskProvider.isServiceTaskPresent(name)

suspend fun addPermanentServiceTask(serviceTask: ServiceTask) =
    CloudControl.serviceTaskProvider.addPermanentServiceTask(serviceTask)

suspend fun removePermanentServiceTask(serviceTask: ServiceTask) =
    CloudControl.serviceTaskProvider.removePermanentServiceTask(serviceTask)

fun ServiceTask.getReservedTaskIds(): Collection<Int> =
    cloudServices.asSequence()
        .filter { it.serviceId.task == this }
        .map { it.serviceId.taskServiceId }
        .toList()