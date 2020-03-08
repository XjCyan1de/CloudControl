package com.github.xjcyan1de.cloudcontrol.api.template

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTemplate
import java.io.File
import java.io.OutputStream
import java.nio.file.Path

interface TemplateStorage {
    fun copy(template: ServiceTemplate, directory: File): Boolean
    fun has(template: ServiceTemplate): Boolean
    fun newOutputStream(template: ServiceTemplate, path: Path): OutputStream
    fun deploy(directory: File, template: ServiceTemplate, predicate: (File) -> Boolean)
}