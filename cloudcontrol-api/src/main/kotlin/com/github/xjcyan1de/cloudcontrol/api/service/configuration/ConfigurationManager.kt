package com.github.xjcyan1de.cloudcontrol.api.service.configuration

import com.github.xjcyan1de.cloudcontrol.api.CloudControl
import com.github.xjcyan1de.cloudcontrol.api.GROUPS_CONFIG_PATH
import com.github.xjcyan1de.cloudcontrol.api.TASKS_DIRECTORY
import com.github.xjcyan1de.cloudcontrol.api.service.*
import com.github.xjcyan1de.cloudcontrol.api.util.save
import com.github.xjcyan1de.cyanlibz.localization.textOf
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.github.config4k.toConfig

object ConfigurationManager {
    var tasks: List<ServiceTask> = emptyList()
        private set
    var groups: List<ServiceGroup> = emptyList()
        private set

    fun load() {
        loadGroups()
        loadTasks()
    }

    private fun loadGroups() {
        val configFile = GROUPS_CONFIG_PATH.toFile()
        if (!configFile.exists()) {
            listOf(
                ServiceGroup("Proxy", emptyList(), emptyList()),
                ServiceGroup("Lobby", emptyList(), emptyList()),
                ServiceGroup(
                    "Global-Lobby", listOf(
                        ServiceTemplate("Global", "bukkit", ServiceStorage.LOCAL)
                    ), emptyList()
                ),
                ServiceGroup(
                    "Global-Proxy", listOf(
                        ServiceTemplate("Global", "proxy", ServiceStorage.LOCAL)
                    ), emptyList()
                )
            ).toConfig("groups").save(configFile)
        }
        groups = ConfigFactory.parseFile(configFile).extract("groups")
    }

    private fun loadTasks() {
        val dir = TASKS_DIRECTORY.toFile()
        if (!dir.exists()) {
            dir.mkdirs()
            ServiceTask(
                name = "Lobby",
                maintenance = false,
                autoDeleteOnStop = false,
                staticServices = false,
                associatedNodes = listOf(CloudControl.currentNetworkNodeSnapshot.node),
                groups = listOf("Lobby", "Global-Server"),
                processConfiguration = ProcessConfiguration(ServiceEnvironment.PAPER),
                startPort = 44955,
                minServiceCount = 1,
                templates = listOf(
                    ServiceTemplate("Lobby", "default", ServiceStorage.LOCAL)
                ),
                deployments = emptyList()
            ).also { saveTask(it) }
            ServiceTask(
                name = "Proxy",
                maintenance = false,
                autoDeleteOnStop = false,
                staticServices = false,
                associatedNodes = listOf(CloudControl.currentNetworkNodeSnapshot.node),
                groups = listOf("Proxy", "Global-Proxy"),
                processConfiguration = ProcessConfiguration(ServiceEnvironment.WATERFALL),
                startPort = 25565,
                minServiceCount = 1,
                templates = listOf(
                    ServiceTemplate("Proxy", "default", ServiceStorage.LOCAL)
                ),
                deployments = emptyList()
            ).also { saveTask(it) }
        }
        tasks = dir.walkTopDown().map { file ->
            try {
                if (file.name.endsWith(".conf")) {
                    println(textOf("cloud_control.load.task", "path" to { file.toString() }).get())
                    ConfigFactory.parseFile(file).extract<ServiceTask>("task").also {
                        println(
                            textOf("cloud_control.load.success",
                                "path" to { file.toString() },
                                "name" to { it.name }).get()
                        )
                    }
                } else null
            } catch (e: Exception) {
                println(textOf("cloud_control.load.task.failed", "path" to { file.toString() }).get())
                e.printStackTrace()
                null
            }
        }.filterNotNull().toList()
    }

    fun saveTask(task: ServiceTask) =
        task.toConfig("task").save(TASKS_DIRECTORY.resolve(task.name + ".conf").toFile())

    fun deleteTask(task: ServiceTask) {
        TASKS_DIRECTORY.resolve(task.name + ".conf").toFile().delete()
    }

    fun save() {
        for (task in tasks) {
            saveTask(task)
        }
        groups.toConfig("groups").save(GROUPS_CONFIG_PATH.toFile())
    }
}