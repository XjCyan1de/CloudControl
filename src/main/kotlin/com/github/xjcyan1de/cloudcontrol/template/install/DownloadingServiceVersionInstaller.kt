package com.github.xjcyan1de.cloudcontrol.template.install

import com.github.xjcyan1de.cloudcontrol.api.console.progressbar.ProgressBarInputStream
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTemplate
import com.github.xjcyan1de.cloudcontrol.api.template.TemplateStorage
import com.github.xjcyan1de.cloudcontrol.api.template.install.ServiceVersion
import com.github.xjcyan1de.cloudcontrol.api.template.install.ServiceVersionInstaller
import com.github.xjcyan1de.cloudcontrol.api.template.install.ServiceVersionType
import java.nio.file.Files
import java.nio.file.Path

class DownloadingServiceVersionInstaller(
    versionType: ServiceVersionType,
    version: ServiceVersion,
    storage: TemplateStorage,
    targetTemplate: ServiceTemplate
) : ServiceVersionInstaller(versionType, version, storage, targetTemplate) {
    override fun install(file: Path) {
        ProgressBarInputStream.wrapDownload(version.url).use { input ->
            storage.newOutputStream(targetTemplate, file).use { output ->
                input.copyTo(output)
            }

            if (!version.isLatest) {
                Files.copy(input, versionCachePath)
            }
        }
    }
}