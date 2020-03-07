package com.github.xjcyan1de.cloudcontrol.api.template.install

import com.github.xjcyan1de.cloudcontrol.api.CACHE_PATH
import com.github.xjcyan1de.cloudcontrol.api.TEMP_DIR_BUILD
import com.github.xjcyan1de.cloudcontrol.api.console.progressbar.ProgressBarInputStream
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTemplate
import com.github.xjcyan1de.cloudcontrol.api.template.TemplateStorage
import com.github.xjcyan1de.cyanlibz.localization.textOf
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

abstract class ServiceVersionInstaller(
    val versionType: ServiceVersionType,
    val version: ServiceVersion,
    val storage: TemplateStorage,
    val targetTemplate: ServiceTemplate
) {
    protected val versionCachePath = Paths.get(CACHE_PATH.toString(), "$versionType-${version.name}")
    protected val workingDirectory: File = File(TEMP_DIR_BUILD.toFile(), UUID.randomUUID().toString())
        get() {
            if (!field.exists()) {
                field.mkdirs()
            }
            return field
        }

    abstract fun install(file: Path)

    fun download(url: URL, target: Path) {
        println(textOf("template_installer.downloading.begin",
            "url" to { url }
        ))
        ProgressBarInputStream.wrapDownload(url).use { input ->
            Files.copy(input, target)
        }
        println(textOf("template_installer.downloading.completed",
            "url" to { url }
        ))
    }
}