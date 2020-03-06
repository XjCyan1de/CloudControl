package com.github.xjcyan1de.cloudcontrol.api.service.configuration

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceDeployment
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTemplate

abstract class BaseServiceConfiguration(
    open var templates: List<ServiceTemplate>,
    open var deployments: List<ServiceDeployment>
)