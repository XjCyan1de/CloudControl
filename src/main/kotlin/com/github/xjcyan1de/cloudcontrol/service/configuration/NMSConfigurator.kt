package com.github.xjcyan1de.cloudcontrol.service.configuration

import com.github.xjcyan1de.cloudcontrol.CloudControlNode
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import java.io.File
import java.util.*

class NMSConfigurator(
    serviceConfiguration: ServiceConfiguration
) : AbstractConfigurator(serviceConfiguration) {
    override fun rewrite(file: File) {
        val properties = Properties()

        file.inputStream().use {
            properties.load(it)
        }

        when (file.nameWithoutExtension) {
            "server" -> {
                properties.setProperty("server-name", serviceConfiguration.serviceId.name)
                properties.setProperty("server-port", serviceConfiguration.port.toString())
                properties.setProperty("server-ip", CloudControlNode.networkNodeConfiguration.hostAddress)
            }
            "eula" -> {
                properties.setProperty("eula", true.toString())
            }
        }

        file.outputStream().use {
            properties.store(it, "Edit by CloudNet")
        }
    }
}