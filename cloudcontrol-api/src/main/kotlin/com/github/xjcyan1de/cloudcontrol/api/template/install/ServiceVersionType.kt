package com.github.xjcyan1de.cloudcontrol.api.template.install

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceEnvironment

data class ServiceVersionType(
    val environment: ServiceEnvironment,
    val installerType: InstallerType,
    val versions: Iterable<ServiceVersion>
)