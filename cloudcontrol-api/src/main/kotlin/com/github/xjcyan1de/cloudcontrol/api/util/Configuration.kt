package com.github.xjcyan1de.cloudcontrol.api.util

import com.typesafe.config.Config
import com.typesafe.config.ConfigRenderOptions
import java.io.File

fun Config.save(file: File) {
    val configRenderOptions = ConfigRenderOptions.defaults().setOriginComments(false).setJson(false)

    root().render(configRenderOptions).also {
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        }
        file.writeText(it)
    }
}
