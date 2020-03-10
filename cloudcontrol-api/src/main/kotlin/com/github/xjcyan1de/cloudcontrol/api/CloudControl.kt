package com.github.xjcyan1de.cloudcontrol.api

const val TPS = 1000

val Any.VERSION: String
    get() = javaClass.`package`.implementationVersion ?: "1.0"

lateinit var CloudControl: CloudControlDriver
