package com.github.xjcyan1de.cloudcontrol.api.service.configuration

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceEnvironment

data class ProcessConfiguration(
    val environment: ServiceEnvironment = ServiceEnvironment.PAPER,
    val maxHeapMemorySize: Int = environment.defaultMaxHeapMemory,
    val jvmOptions: List<String> = emptyList()
) {
    companion object
}