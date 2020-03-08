package com.github.xjcyan1de.cloudcontrol.service.configuration

import com.github.xjcyan1de.cloudcontrol.CloudControlNode
import com.github.xjcyan1de.cloudcontrol.api.config.Config
import com.github.xjcyan1de.cloudcontrol.api.config.ConfigType
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import java.io.File

class BungeeConfigurator(
    serviceConfiguration: ServiceConfiguration
) : AbstractConfigurator(serviceConfiguration) {
    val host = CloudControlNode.networkNodeConfiguration.hostAddress + ":" + serviceConfiguration.port

    @Suppress("UNCHECKED_CAST")
    override fun rewrite(file: File) {
        val config = Config.load(file, ConfigType.YAML)
        val listeners = config["listeners", ArrayList<Any>()]!!
        val element = (listeners.firstOrNull() ?: HashMap<String, Any>().also {
            listeners.add(it)
        }) as MutableMap<String, Any>

        element["host"] = host
        config["listeners"] = listeners
        config.save(file, ConfigType.YAML)
    }
}