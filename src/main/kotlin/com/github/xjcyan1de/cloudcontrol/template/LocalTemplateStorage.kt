package com.github.xjcyan1de.cloudcontrol.template

import com.github.xjcyan1de.cloudcontrol.api.LOCALE_TEMPLATE_DIR
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTemplate
import com.github.xjcyan1de.cloudcontrol.api.template.TemplateStorage
import java.io.File
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

object LocalTemplateStorage : TemplateStorage {
    val storageDirectory = LOCALE_TEMPLATE_DIR.toFile()

    override fun copy(template: ServiceTemplate, directory: File): Boolean {
        val templateDirectory = File(storageDirectory, template.templatePath)
        return templateDirectory.copyRecursively(directory, true) { _, e ->
            e.printStackTrace()
            OnErrorAction.TERMINATE
        }
    }

    override fun has(template: ServiceTemplate): Boolean =
        File(storageDirectory, template.templatePath).exists()

    override fun newOutputStream(template: ServiceTemplate, path: Path): OutputStream {
        val file: Path = storageDirectory.toPath().resolve(template.templatePath).resolve(path)
        if (Files.exists(file)) {
            Files.delete(file)
        } else {
            Files.createDirectories(file.parent)
        }
        return Files.newOutputStream(file, StandardOpenOption.CREATE)
    }

    override fun deploy(directory: File, template: ServiceTemplate, predicate: (File) -> Boolean) {
        TODO("Not yet implemented")
    }
}