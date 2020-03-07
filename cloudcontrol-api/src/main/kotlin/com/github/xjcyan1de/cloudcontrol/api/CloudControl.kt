package com.github.xjcyan1de.cloudcontrol.api

import java.nio.file.Paths

const val TPS = 1000
val USER get() = System.getProperty("user.name")

val LAUNCHER_SELECT_VERSION get() = System.getProperty("cloudcontrol.launcher.select.version", "1.0")
val CONSOLE_PROMT get() = System.getProperty("cloudcontrol.console.prompt", "§b%user%§r@§7%screen% §f=> §r")
val TEMP_DIR get() = Paths.get(System.getProperty("cloudcontrol.tempDir", "temp"))
val LOCAL_DIR get() = Paths.get(System.getProperty("cloudcontrol.tempDir", "local"))
val CONFIG_PATH get() = Paths.get(System.getProperty("cloudcontrol.config.path", "config.conf"))
val GROUPS_CONFIG_PATH = Paths.get(System.getProperty("cloudcontrol.config.groups.path", "local/groups.conf"))
val TASKS_DIRECTORY = Paths.get(System.getProperty("cloudcontrol.config.tasks.directory.path", "local/tasks"))

val Any.VERSION: String
    get() = javaClass.`package`.implementationVersion ?: "1.0"

val CLUSTER_ID: String? get() = System.getenv("CLOUDCONTROL_CLUSTER_ID")
val CLUSTER_NODE_UNIQUE_ID: String? get() = System.getenv("CLOUDCONTROL_CLUSTER_NODE_UNIQUE_ID")
val RUNTIME_JVM_COMMAND: String? get() = System.getenv("CLOUDCONTROL_RUNTIME_JVM_COMMAND")

lateinit var CloudControl: CloudControlDriver
