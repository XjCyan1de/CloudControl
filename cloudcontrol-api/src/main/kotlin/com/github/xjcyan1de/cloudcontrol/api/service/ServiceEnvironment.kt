package com.github.xjcyan1de.cloudcontrol.api.service

enum class ServiceEnvironment(val defaultMaxHeapMemory: Int = 128) {
    PAPER(384),
    WATERFALL,
    VELOCITY,
    BUNGEECORD;

    companion object
}