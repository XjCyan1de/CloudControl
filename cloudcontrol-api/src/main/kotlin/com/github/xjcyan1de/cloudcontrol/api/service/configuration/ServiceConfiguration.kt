package com.github.xjcyan1de.cloudcontrol.api.service.configuration

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceDeployment
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceId
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTask
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTemplate

data class ServiceConfiguration(
    var serviceId: ServiceId,
    val runtime: String,
    var groups: List<String>,
    override var templates: List<ServiceTemplate>,
    override var deployments: List<ServiceDeployment>,
    var deletedFilesAfterStop: List<String>,
    var processConfiguration: ProcessConfiguration,
    var port: Int
) : BaseServiceConfiguration(templates, deployments)

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