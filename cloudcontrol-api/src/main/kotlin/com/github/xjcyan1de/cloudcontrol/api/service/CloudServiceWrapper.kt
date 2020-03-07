package com.github.xjcyan1de.cloudcontrol.api.service

import com.github.xjcyan1de.cloudcontrol.api.CloudControl
import java.util.*

interface CloudServiceWrapper {
    val serviceInfoSnapshot: ServiceInfoSnapshot
    suspend fun getCachedLogMessages(): Queue<String>
    suspend fun addServiceTemplate(serviceTemplate: ServiceTemplate)
    suspend fun addServiceDeployment(serviceDeployment: ServiceDeployment)
    suspend fun stop() = setLifeCycle(ServiceLifeCycle.STOPPED)
    suspend fun start() = setLifeCycle(ServiceLifeCycle.RUNNING)
    suspend fun delete() = setLifeCycle(ServiceLifeCycle.DELETED)
    suspend fun setLifeCycle(lifeCycle: ServiceLifeCycle)
    suspend fun restart()
    suspend fun kill()
    suspend fun runCommand(command: String)
    suspend fun deployResources(removeDeployments: Boolean = true)

    companion object
}

fun getCloudServiceWrapper(uniqueId: UUID): CloudServiceWrapper =
    CloudControl.getCloudServiceWrapper(uniqueId) ?: error("Service '$uniqueId' not found.")

fun getCloudServiceWrapper(name: String): CloudServiceWrapper =
    CloudControl.getCloudServiceWrapper(name) ?: error("Service '$name' not found.")

fun ServiceInfoSnapshot.toWrapper(): CloudServiceWrapper =
    CloudControl.getCloudServiceWrapper(this)

suspend fun getCachedLogMessagesFromCloudService(serviceInfoSnapshot: ServiceInfoSnapshot) =
    serviceInfoSnapshot.toWrapper().getCachedLogMessages()

suspend fun getCachedLogMessagesFromCloudService(serviceUniqueId: UUID) =
    getCloudServiceWrapper(serviceUniqueId).getCachedLogMessages()

suspend fun addServiceTemplateToCloudService(serviceInfoSnapshot: ServiceInfoSnapshot, template: ServiceTemplate) =
    serviceInfoSnapshot.toWrapper().addServiceTemplate(template)

suspend fun addServiceTemplateToCloudService(serviceUniqueId: UUID, template: ServiceTemplate) =
    getCloudServiceWrapper(serviceUniqueId).addServiceTemplate(template)

suspend fun addServiceDeploymentToCloudService(
    serviceInfoSnapshot: ServiceInfoSnapshot,
    deployment: ServiceDeployment
) = serviceInfoSnapshot.toWrapper().addServiceDeployment(deployment)

suspend fun addServiceDeploymentToCloudService(
    serviceUniqueId: UUID,
    deployment: ServiceDeployment
) = getCloudServiceWrapper(serviceUniqueId).addServiceDeployment(deployment)

suspend fun stopCloudService(serviceInfoSnapshot: ServiceInfoSnapshot) =
    serviceInfoSnapshot.toWrapper().stop()

suspend fun stopCloudService(serviceUniqueId: UUID) =
    getCloudServiceWrapper(serviceUniqueId).stop()

suspend fun startCloudService(serviceInfoSnapshot: ServiceInfoSnapshot) =
    serviceInfoSnapshot.toWrapper().start()

suspend fun startCloudService(serviceUniqueId: UUID) =
    getCloudServiceWrapper(serviceUniqueId).start()

suspend fun deleteCloudService(serviceInfoSnapshot: ServiceInfoSnapshot) =
    serviceInfoSnapshot.toWrapper().delete()

suspend fun deleteCloudService(serviceUniqueId: UUID) =
    getCloudServiceWrapper(serviceUniqueId).delete()

suspend fun setCloudServiceLifeCycle(serviceInfoSnapshot: ServiceInfoSnapshot, lifeCycle: ServiceLifeCycle) =
    serviceInfoSnapshot.toWrapper().setLifeCycle(lifeCycle)

suspend fun setCloudServiceLifeCycle(serviceUniqueId: UUID, lifeCycle: ServiceLifeCycle) =
    getCloudServiceWrapper(serviceUniqueId).setLifeCycle(lifeCycle)

suspend fun restartCloudService(serviceInfoSnapshot: ServiceInfoSnapshot) =
    serviceInfoSnapshot.toWrapper().restart()

suspend fun restartCloudService(serviceUniqueId: UUID) =
    getCloudServiceWrapper(serviceUniqueId).restart()

suspend fun killCloudService(serviceInfoSnapshot: ServiceInfoSnapshot) =
    serviceInfoSnapshot.toWrapper().kill()

suspend fun killCloudService(serviceUniqueId: UUID) =
    getCloudServiceWrapper(serviceUniqueId).kill()

suspend fun runCommand(serviceUniqueId: UUID, command: String) =
    getCloudServiceWrapper(serviceUniqueId).runCommand(command)

suspend fun runCommand(serviceInfoSnapshot: ServiceInfoSnapshot, command: String) =
    serviceInfoSnapshot.toWrapper().runCommand(command)

suspend fun deployResources(serviceUniqueId: UUID, removeDeployments: Boolean = true): Unit =
    getCloudServiceWrapper(serviceUniqueId).deployResources(removeDeployments)

suspend fun deployResources(serviceInfoSnapshot: ServiceInfoSnapshot, removeDeployments: Boolean = true) =
    serviceInfoSnapshot.toWrapper().deployResources(removeDeployments)