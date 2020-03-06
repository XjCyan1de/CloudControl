package com.github.xjcyan1de.cloudcontrol.api.service

import com.github.xjcyan1de.cloudcontrol.api.cloudControlDriver
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkAddress
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import java.time.Instant

data class ServiceInfoSnapshot(
    val creationTime: Instant,
    var serviceId: ServiceId,
    var address: NetworkAddress,
    var connected: Boolean,
    var lifeCycle: ServiceLifeCycle,
    var processSnapshot: ProcessSnapshot,
    var serviceConfiguration: ServiceConfiguration
) {
    companion object {
        fun create(serviceConfiguration: ServiceConfiguration, lifeCycle: ServiceLifeCycle): ServiceInfoSnapshot =
            ServiceInfoSnapshot(
                Instant.now(),
                serviceConfiguration.serviceId,
                NetworkAddress(cloudControlDriver.nodeConfiguration.hostAddress, serviceConfiguration.port),
                false,
                lifeCycle,
                ProcessSnapshot(-1, -1, -1, -1, -1, -1, emptyList(), -1.0, -1),
                serviceConfiguration
            )
    }
}

fun ServiceInfoSnapshot(serviceConfiguration: ServiceConfiguration, lifeCycle: ServiceLifeCycle) =
    ServiceInfoSnapshot.create(serviceConfiguration, lifeCycle)