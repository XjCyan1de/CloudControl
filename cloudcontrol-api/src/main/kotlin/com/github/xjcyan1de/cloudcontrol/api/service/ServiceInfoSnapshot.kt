package com.github.xjcyan1de.cloudcontrol.api.service

import com.github.xjcyan1de.cloudcontrol.api.network.NetworkAddress
import java.time.Instant

data class ServiceInfoSnapshot(
    val creationTime: Instant,
    var serviceId: ServiceId,
    var address: NetworkAddress,
    var connected: Boolean,
    var lifeCycle: ServiceLifeCycle,
    var processSnapshot: ProcessSnapshot
)