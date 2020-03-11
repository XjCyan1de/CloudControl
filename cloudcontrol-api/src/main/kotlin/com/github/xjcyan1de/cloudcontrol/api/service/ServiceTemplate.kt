package com.github.xjcyan1de.cloudcontrol.api.service

import java.time.Instant

data class ServiceTemplate(
    val prefix: String,
    val name: String,
    val storage: ServiceStorage,
    val isShouldAlwaysCopyToStaticServices: Boolean = false
) {
    var lastUpdate: Instant = Instant.MIN

    override fun toString(): String = "${storage.name}:$templatePath"

    val templatePath: String get() = "$prefix/$name"
}