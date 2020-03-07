package com.github.xjcyan1de.cloudcontrol.api.service.configuration

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceDeployment
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceId
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTemplate

data class ServiceConfiguration(
    val serviceId: ServiceId,
    val autoDeleteOnStop: Boolean,
    val staticService: Boolean,
    val port: Int,
    val groups: Collection<String> = emptyList(),
    val deletedFilesAfterStop: Iterable<String> = emptyList(),
    override val templates: Iterable<ServiceTemplate> = emptyList(),
    override val deployments: Iterable<ServiceDeployment> = emptyList(),
    override val properties: Map<String, String> = emptyMap(),
    val processConfiguration: ProcessConfiguration = ProcessConfiguration()
) : BaseServiceConfiguration(templates, deployments, properties) {
    companion object
}