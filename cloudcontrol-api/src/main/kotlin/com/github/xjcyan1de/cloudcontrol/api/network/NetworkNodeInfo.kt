package com.github.xjcyan1de.cloudcontrol.api.network

import com.github.xjcyan1de.cloudcontrol.api.service.ProcessSnapshot
import java.time.Instant

data class NetworkNodeInfo(
    var creationTime: Instant,
    var node: NetworkNode,
    var version: String,
    var currentServicesCount: Int,
    var usedMemory: Int,
    var reservedMemory: Int,
    var maxMemory: Int,
    var processSnapshot: ProcessSnapshot,
    var systemCpuUsage: Double
)