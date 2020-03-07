package com.github.xjcyan1de.cloudcontrol.api.command

import com.google.gson.JsonObject

interface TabCompleter {
    fun complete(
        commandLine: String,
        args: Array<String>,
        properties: JsonObject
    ): Collection<String>
}