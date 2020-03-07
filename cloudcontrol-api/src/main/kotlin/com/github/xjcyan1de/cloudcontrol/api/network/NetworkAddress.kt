package com.github.xjcyan1de.cloudcontrol.api.network

import java.net.InetSocketAddress
import java.net.SocketAddress

data class NetworkAddress(
    val host: String,
    val port: Int
) {
    constructor(socketAddress: InetSocketAddress) : this(socketAddress.address.hostAddress, socketAddress.port)
    constructor(socketAddress: SocketAddress) : this(
        socketAddress.toString().split(":")[0].replaceFirst("/", ""),
        socketAddress.toString().split(":")[1].toInt()
    )

    override fun toString(): String = "$host:$port"

    companion object
}