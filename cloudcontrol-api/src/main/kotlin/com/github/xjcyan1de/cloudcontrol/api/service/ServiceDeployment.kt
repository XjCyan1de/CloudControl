package com.github.xjcyan1de.cloudcontrol.api.service

data class ServiceDeployment(
    val template: ServiceTemplate,
    val excludes: Collection<String>
)