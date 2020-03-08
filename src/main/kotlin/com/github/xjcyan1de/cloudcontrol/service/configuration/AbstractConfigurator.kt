package com.github.xjcyan1de.cloudcontrol.service.configuration

import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import java.io.File

abstract class AbstractConfigurator(
    val serviceConfiguration: ServiceConfiguration
) {
    open fun rewrite(file: File) {
        val lines = file.readLines()
        val replacedLines = lines.map { line ->
            val iterator = line.toCharArray().iterator()
            var index = 0
            while (iterator.hasNext()) {
                val char = iterator.nextChar()
                if (char != ' ') break
                index++
            }
            " ".repeat(index) + rewriteLine(file, line.substring(index))
        }.joinToString(System.lineSeparator())

        file.writeText(replacedLines)
    }

    protected open fun rewriteLine(file: File, line: String): String = line
}