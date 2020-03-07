package com.github.xjcyan1de.cloudcontrol.template.install

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTemplate
import com.github.xjcyan1de.cloudcontrol.api.template.TemplateStorage
import com.github.xjcyan1de.cloudcontrol.api.template.install.ServiceVersion
import com.github.xjcyan1de.cloudcontrol.api.template.install.ServiceVersionInstaller
import com.github.xjcyan1de.cloudcontrol.api.template.install.ServiceVersionType
import java.nio.file.Path

class ProcessingServiceVersionInstaller(
    versionType: ServiceVersionType,
    version: ServiceVersion,
    storage: TemplateStorage,
    targetTemplate: ServiceTemplate
) : ServiceVersionInstaller(versionType, version, storage, targetTemplate) {
    override fun install(file: Path) {
        TODO()
    }
}