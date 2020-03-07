package com.github.xjcyan1de.cloudcontrol.api.service.configuration

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceDeployment
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTemplate

abstract class BaseServiceConfiguration(
    open val templates: List<ServiceTemplate>,
    open val deployments: List<ServiceDeployment>
) {
    companion object
}