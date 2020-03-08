package com.github.xjcyan1de.cloudcontrol.api

import java.nio.file.Paths

val USER get() = System.getProperty("user.name")

val LAUNCHER_SELECT_VERSION = System.getProperty("cloudcontrol.launcher.select.version", "1.0")
val CONSOLE_PROMT = System.getProperty("cloudcontrol.console.prompt", "§b%user%§r@§7%screen% §f=> §r")
val TEMP_DIR = Paths.get(System.getProperty("cloudcontrol.tempDir", "temp"))
val LOCAL_DIR = Paths.get(System.getProperty("cloudcontrol.tempDir", "local"))
val PRESISTANCE_SERVICES_DIR = Paths.get(System.getProperty("cloudcontrol.persistable.services.path", "local/services"))
val LOCALE_TEMPLATE_DIR = Paths.get(System.getProperty("cloudcontrol.storage.local", "local/templates"))
val CONFIG_PATH = Paths.get(System.getProperty("cloudcontrol.config.path", "config.conf"))
val GROUPS_CONFIG_PATH = Paths.get(System.getProperty("cloudcontrol.config.groups.path", "local/groups.conf"))
val TASKS_DIRECTORY = Paths.get(System.getProperty("cloudcontrol.config.tasks.directory.path", "local/tasks"))
val TEMP_DIR_BUILD = Paths.get(System.getProperty("cloudcontrol.tempDir.build"))
val CACHE_PATH = Paths.get(System.getProperty("cloudcontrol.cache.path", "local/cache"))
val SERVICE_ERROR_RESTART_DELAY: Long = System.getProperty("cloudcontrol.serviceErrorRestartDelay").toLongOrNull() ?: 30
