package com.github.xjcyan1de.cloudcontrol.api.service.configuration

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceDeployment
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTemplate

abstract class BaseServiceConfiguration(
    open val templates: Iterable<ServiceTemplate>,
    open val deployments: Iterable<ServiceDeployment>,
    open val properties: Map<String, String>
) {
    companion object
}