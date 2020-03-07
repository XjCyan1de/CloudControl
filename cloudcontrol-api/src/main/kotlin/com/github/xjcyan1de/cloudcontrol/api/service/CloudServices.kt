package com.github.xjcyan1de.cloudcontrol.api.service

import com.github.xjcyan1de.cloudcontrol.api.CloudControl
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkAddress
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.util.*

data class ServiceInfoSnapshot(
    val creationTime: Instant,
    var serviceId: ServiceId,
    var address: NetworkAddress,
    var connected: Boolean,
    var lifeCycle: ServiceLifeCycle,
    var processSnapshot: ProcessSnapshot,
    var serviceConfiguration: ServiceConfiguration
) {
    constructor(
        serviceConfiguration: ServiceConfiguration, lifeCycle: ServiceLifeCycle
    ) : this(
        Instant.now(),
        serviceConfiguration.serviceId,
        NetworkAddress(CloudControl.networkNodeConfiguration.hostAddress, serviceConfiguration.port),
        false,
        lifeCycle,
        ProcessSnapshot(-1, -1, -1, -1, -1, -1, emptyList(), -1.0, -1),
        serviceConfiguration
    )

    companion object
}

interface GeneralCloudServiceProvider {
    suspend fun getServicesUniqueIds(): Collection<UUID>
    suspend fun getCloudServices(): Collection<ServiceInfoSnapshot>
    suspend fun getStartedCloudServices(): Collection<ServiceInfoSnapshot>
    suspend fun getServicesCount(): Int
    suspend fun getCloudServices(serviceTask: ServiceTask): Collection<ServiceInfoSnapshot>
    suspend fun getCloudServices(serviceEnvironment: ServiceEnvironment): Collection<ServiceInfoSnapshot>
    suspend fun getCloudServices(serviceGroup: ServiceGroup): Collection<ServiceInfoSnapshot>
    suspend fun getServicesCount(serviceGroup: ServiceGroup): Int
    suspend fun getServicesCount(serviceTask: ServiceTask): Int
    suspend fun getServicesCount(serviceEnvironment: ServiceEnvironment): Int
    suspend fun getCloudService(name: String): ServiceInfoSnapshot?
    suspend fun getCloudService(uniqueId: UUID): ServiceInfoSnapshot?

    companion object
}

val servicesUniqueIds: Collection<UUID>
    get() = runBlocking { CloudControl.generalCloudServiceProvider.getServicesUniqueIds() }
val cloudServices: Collection<ServiceInfoSnapshot>
    get() = runBlocking { CloudControl.generalCloudServiceProvider.getCloudServices() }
val startedCloudServices: Collection<ServiceInfoSnapshot>
    get() = runBlocking { CloudControl.generalCloudServiceProvider.getStartedCloudServices() }
val servicesCount: Int
    get() = runBlocking { CloudControl.generalCloudServiceProvider.getServicesCount() }

val ServiceTask.cloudServices: Collection<ServiceInfoSnapshot>
    get() = runBlocking { CloudControl.generalCloudServiceProvider.getCloudServices(this@cloudServices) }
val ServiceEnvironment.cloudServices: Collection<ServiceInfoSnapshot>
    get() = runBlocking { CloudControl.generalCloudServiceProvider.getCloudServices(this@cloudServices) }
val ServiceGroup.cloudServices: Collection<ServiceInfoSnapshot>
    get() = runBlocking { CloudControl.generalCloudServiceProvider.getCloudServices(this@cloudServices) }

val ServiceTask.servicesCount: Int
    get() = runBlocking { CloudControl.generalCloudServiceProvider.getServicesCount(this@servicesCount) }
val ServiceEnvironment.servicesCount: Int
    get() = runBlocking { CloudControl.generalCloudServiceProvider.getServicesCount(this@servicesCount) }
val ServiceGroup.servicesCount: Int
    get() = runBlocking { CloudControl.generalCloudServiceProvider.getServicesCount(this@servicesCount) }

suspend fun getCloudService(name: String): ServiceInfoSnapshot? =
    CloudControl.generalCloudServiceProvider.getCloudService(name)

suspend fun getCloudService(uniqueId: UUID): ServiceInfoSnapshot? =
    CloudControl.generalCloudServiceProvider.getCloudService(uniqueId)

val Iterable<ServiceInfoSnapshot>.currentUsedHeapMemory: Int
    get() = asSequence()
        .filter { it.lifeCycle == ServiceLifeCycle.RUNNING }
        .sumBy { it.serviceConfiguration.processConfiguration.maxHeapMemorySize }

val Iterable<ServiceInfoSnapshot>.currentReservedMemory: Int
    get() = asSequence()
        .sumBy { it.serviceConfiguration.processConfiguration.maxHeapMemorySize }