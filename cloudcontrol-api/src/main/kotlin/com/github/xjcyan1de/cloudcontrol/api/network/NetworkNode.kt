package com.github.xjcyan1de.cloudcontrol.api.network

data class NetworkNode(
    val name: String
) {
    override fun hashCode(): Int = name.hashCode()

    override fun equals(other: Any?): Boolean = name.equals((other as? NetworkNode)?.name, true)
}