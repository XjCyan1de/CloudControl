package com.github.xjcyan1de.cloudcontrol.api.service

enum class ServiceType(
    val id: String,
    val environment: ServiceEnvironment
) {
    MINECRAFT_SERVER_VANILLA("vanilla", ServiceEnvironment.MINECRAFT_SERVER),
    MINECRAFT_SERVER_SPIGOT("spigot", ServiceEnvironment.MINECRAFT_SERVER),
    MINECRAFT_SERVER_PAPER("paper", ServiceEnvironment.MINECRAFT_SERVER),
    MINECRAFT_SERVER_SPONGE("sponge", ServiceEnvironment.MINECRAFT_SERVER),
    PROXY_SERVER_BUNGEECORD("bungeecord", ServiceEnvironment.PROXY_SERVER),
    PROXY_SERVER_WATERFALL("waterfall", ServiceEnvironment.PROXY_SERVER),
    PROXY_SERVER_VELOCITY("velocity", ServiceEnvironment.PROXY_SERVER),
}