package com.github.xjcyan1de.cloudcontrol.command

import com.github.xjcyan1de.cloudcontrol.common.Properties

interface TabCompleter {
    fun complete(
        commandLine: String,
        args: Array<String>,
        properties: Properties
    ): Collection<String>
}