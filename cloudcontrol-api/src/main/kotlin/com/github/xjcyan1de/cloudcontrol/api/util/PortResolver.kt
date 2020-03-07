package com.github.xjcyan1de.cloudcontrol.api.util

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
}