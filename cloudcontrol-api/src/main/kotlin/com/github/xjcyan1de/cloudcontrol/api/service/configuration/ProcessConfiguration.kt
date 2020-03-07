package com.github.xjcyan1de.cloudcontrol.api.service.configuration

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceEnvironment

data class ProcessConfiguration(
    val environment: ServiceEnvironment,
    val maxHeapMemorySize: Int,
    val jvmOptions: List<String>
) {
    companion object
}