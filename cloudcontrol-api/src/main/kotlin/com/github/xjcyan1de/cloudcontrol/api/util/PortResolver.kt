package com.github.xjcyan1de.cloudcontrol.api.util

import com.github.xjcyan1de.cloudcontrol.api.service.cloudServices
import com.github.xjcyan1de.cyanlibz.localization.textOf
import java.net.InetSocketAddress
import java.net.ServerSocket

object PortResolver {
    fun checkPort(port: Int): Boolean {
        try {
            ServerSocket().use { serverSocket ->
                serverSocket.bind(InetSocketAddress(port))
                return true
            }
        } catch (exception: Exception) {
            return false
        }
    }

    fun checkHost(host: String, port: Int): Boolean {
        try {
            ServerSocket().use { serverSocket ->
                serverSocket.bind(InetSocketAddress(host, port))
                return true
            }
        } catch (exception: Exception) {
            return false
        }
    }

    fun findFreePort(startPort: Int): Int {
        var port = startPort
        while (!checkPort(port)) {
            ++port
        }
        return port
    }

    fun resolvePort(startPort: Int = 44955): Int {
        var port = startPort
        val ports = cloudServices.map { it.serviceConfiguration.port }

        while (ports.contains(port)) {
            port++
        }

        while (!checkPort(port)) {
            println(textOf("cloud_control.service.service_port_bind_retry_message",
                "port" to { port },
                "next_port" to { ++port }
            ).get())
        }

        return port
    }
}