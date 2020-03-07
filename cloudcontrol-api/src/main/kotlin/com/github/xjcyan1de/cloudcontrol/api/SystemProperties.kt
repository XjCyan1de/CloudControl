package com.github.xjcyan1de.cloudcontrol.api

import java.nio.file.Paths

val USER get() = System.getProperty("user.name")

val LAUNCHER_SELECT_VERSION
    get() = System.getProperty("cloudcontrol.launcher.select.version", "1.0")
val CONSOLE_PROMT
    get() = System.getProperty("cloudcontrol.console.prompt", "§b%user%§r@§7%screen% §f=> §r")
val TEMP_DIR
    get() = Paths.get(System.getProperty("cloudcontrol.tempDir", "temp"))
val LOCAL_DIR
    get() = Paths.get(System.getProperty("cloudcontrol.tempDir", "local"))
val PRESISTANCE_SERVICES_DIR
    get() = Paths.get(System.getProperty("cloudcontrol.persistable.services.path", "local/services"))
val LOCALE_TEMPLATE_DIR
    get() = Paths.get(System.getProperty("cloudcontrol.storage.local", "local/templates"))
val CONFIG_PATH
    get() = Paths.get(System.getProperty("cloudcontrol.config.path", "config.conf"))
val GROUPS_CONFIG_PATH
    get() = Paths.get(System.getProperty("cloudcontrol.config.groups.path", "local/groups.conf"))
val TASKS_DIRECTORY
    get() = Paths.get(System.getProperty("cloudcontrol.config.tasks.directory.path", "local/tasks"))
val TEMP_DIR_BUILD
    get() = Paths.get(System.getProperty("cloudcontrol.tempDir.build"))
val CACHE_PATH
    get() = Paths.get(System.getProperty("cloudcontrol.cache.path", "local/cache"))