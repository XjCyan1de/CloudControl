package com.github.xjcyan1de.cloudcontrol.api

const val TPS = 1000

val Any.VERSION: String
    get() = javaClass.`package`.implementationVersion ?: "1.0"

val CLUSTER_ID: String? get() = System.getenv("CLOUDCONTROL_CLUSTER_ID")
val CLUSTER_NODE_UNIQUE_ID: String? get() = System.getenv("CLOUDCONTROL_CLUSTER_NODE_UNIQUE_ID")
val RUNTIME_JVM_COMMAND: String? get() = System.getenv("CLOUDCONTROL_RUNTIME_JVM_COMMAND")

lateinit var CloudControl: CloudControlDriver
