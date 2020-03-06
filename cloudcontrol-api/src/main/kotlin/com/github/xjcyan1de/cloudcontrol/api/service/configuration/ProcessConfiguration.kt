package com.github.xjcyan1de.cloudcontrol.api.service.configuration

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceEnvironment

data class ProcessConfiguration(
    var environment: ServiceEnvironment,
    var maxHeapMemorySize: Int,
    var jvmOptions: List<String>
)