package com.github.xjcyan1de.cloudcontrol.api.service.configuration

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceDeployment
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceId
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTask
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTemplate

data class ServiceConfiguration(
    val serviceId: ServiceId,
    val runtime: String,
    val groups: List<String>,
    override val templates: List<ServiceTemplate>,
    override val deployments: List<ServiceDeployment>,
    val deletedFilesAfterStop: List<String>,
    val processConfiguration: ProcessConfiguration,
    val port: Int
) : BaseServiceConfiguration(templates, deployments) {
    companion object
}

fun ServiceTask.toServiceConfiguration(serviceId: ServiceId) = ServiceConfiguration(
    serviceId,
    runtime,
    groups,
    emptyList(),
    emptyList(),
    emptyList(),
    processConfiguration.copy(),
    startPort
)