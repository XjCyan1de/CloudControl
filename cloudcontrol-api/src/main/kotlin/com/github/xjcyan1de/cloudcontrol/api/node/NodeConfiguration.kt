package com.github.xjcyan1de.cloudcontrol.api.node

import com.github.xjcyan1de.cloudcontrol.api.*
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkCluster
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNode
import com.github.xjcyan1de.cloudcontrol.api.util.SystemStatistics
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.github.config4k.toConfig
import java.util.*
import kotlin.math.min
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class NodeConfiguration(
    identity: NetworkNode = NetworkNode(
        if (CLUSTER_NODE_UNIQUE_ID != null) CLUSTER_NODE_UNIQUE_ID!! else
            "Node-${UUID.randomUUID().toString().split("-")[0]}"
    ),
    cluster: NetworkCluster = NetworkCluster(
        if (CLUSTER_ID != null) UUID.fromString(CLUSTER_ID!!) else UUID.randomUUID(),
        emptyList()
    ),
    maxCPUUsageToStartServices: Double = 100.0,
    parallelServiceStartSequence: Boolean = true,
    maxMemory: Int = (SystemStatistics.systemMemory / 1048576 - min(
        SystemStatistics.systemMemory / 1048576,
        2048
    )).toInt(),
    maxServiceConsoleLogCacheSize: Int = 64,
    printErrorStreamLinesFromServices: Boolean = true,
    defaultJVMOptionParameters: Boolean = true,
    jvmCommand: String = if (RUNTIME_JVM_COMMAND != null) RUNTIME_JVM_COMMAND!! else "java"
) {
    var identity by ConfigSaver(identity)
    var cluster by ConfigSaver(cluster)
    var maxCPUUsageToStartServices by ConfigSaver(
        maxCPUUsageToStartServices
    )
    var parallelServiceStartSequence by ConfigSaver(
        parallelServiceStartSequence
    )
    var maxMemory by ConfigSaver(
        maxMemory
    )
    var maxServiceConsoleLogCacheSize by ConfigSaver(
        maxServiceConsoleLogCacheSize
    )
    var printErrorStreamLinesFromServices by ConfigSaver(
        printErrorStreamLinesFromServices
    )
    var defaultJVMOptionParameters by ConfigSaver(
        defaultJVMOptionParameters
    )
    var jvmCommand by ConfigSaver(
        jvmCommand
    )

    class ConfigSaver<R, T>(var value: T) : ReadWriteProperty<R, T> {
        override fun getValue(thisRef: R, property: KProperty<*>): T = value

        override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
            this.value = value
            val configFile = CONFIG_PATH.toFile()
            toConfig("config").save(configFile)
        }
    }

    companion object {
        fun load(): NodeConfiguration {
            val configFile = CONFIG_PATH.toFile()
            if (!configFile.exists()) {
                NodeConfiguration().toConfig("config").save(configFile)
            }
            return ConfigFactory.parseFile(configFile).extract("config")
        }
    }
}