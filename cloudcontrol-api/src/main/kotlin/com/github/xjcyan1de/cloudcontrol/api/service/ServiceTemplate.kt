package com.github.xjcyan1de.cloudcontrol.api.service

data class ServiceTemplate(
    val prefix: String,
    val name: String,
    val storage: ServiceStorage
) {
    override fun toString(): String = "${storage.name}:$prefix/$name"

    val templatePath: String get() = "$prefix:$name"
}