package com.github.xjcyan1de.cloudcontrol.api.service

import java.util.*

data class ServiceId(
    val uniqueId: UUID,
    val nodeName: String,
    val task: ServiceTask,
    val taskServiceId: Int,
    val environment: ServiceEnvironment
) {
    val name: String = "${task.name}-$taskServiceId"
}