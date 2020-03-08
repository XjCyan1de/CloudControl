package com.github.xjcyan1de.cloudcontrol.api.network

import com.github.xjcyan1de.cloudcontrol.api.CLUSTER_ID
import com.github.xjcyan1de.cloudcontrol.api.CLUSTER_NODE_UNIQUE_ID
import com.github.xjcyan1de.cloudcontrol.api.CONFIG_PATH
import com.github.xjcyan1de.cloudcontrol.api.RUNTIME_JVM_COMMAND
import com.github.xjcyan1de.cloudcontrol.api.util.SystemStatistics
import com.github.xjcyan1de.cloudcontrol.api.util.save
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.github.config4k.toConfig
import java.net.InetAddress
import java.util.*
import kotlin.math.min
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class NetworkNodeConfiguration(
    identity: NetworkNode = NetworkNode(
        if (CLUSTER_NODE_UNIQUE_ID != null) CLUSTER_NODE_UNIQUE_ID!! else
            "Node-${UUID.randomUUID().toString().split("-")[0]}"
    ),
    cluster: NetworkCluster = NetworkCluster(
        if (CLUSTER_ID != null) UUID.fromString(CLUSTER_ID!!) else UUID.randomUUID(),
        listOf(identity)
    ),
    hostAddress: String = InetAddress.getLocalHost().hostAddress,
    maxCPUUsageToStartServices: Double = 100.0,
    parallelServiceStartSequence: Boolean = true,
    maxMemory: Int = (SystemStatistics.systemMemory / 1048576 - min(
        SystemStatistics.systemMemory / 1048576,
        2048
    )).toInt(),
    maxServiceConsoleLogCacheSize: Int = 64,
    printErrorStreamLinesFromServices: Boolean = true,
    defaultJVMOptionParameters: Iterable<String> = listOf(
        "-server",
        "-XX:+AlwaysPreTouch",
        "-XX:+UseStringDeduplication",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+UseCompressedOops",

        "-XX:-TieredCompilation",
        "-XX:CompileThreshold=100",

        "-XX:+UseG1GC",
        "-XX:G1NewSizePercent=50",
        "-XX:G1MaxNewSizePercent=80",
        "-XX:G1MixedGCLiveThresholdPercent=35",
        "-XX:MaxGCPauseMillis=100",
        "-XX:TargetSurvivorRatio=90",
        "-XX:+ParallelRefProcEnabled",
        "-XX:-UseAdaptiveSizePolicy",

        "-Dfile.encoding=UTF-8",
        "-Dclient.encoding.override=UTF-8",
        "-Dio.netty.noPreferDirect=true",
        "-Dio.netty.maxDirectMemory=0",
        "-Dio.netty.leakDetectionLevel=DISABLED",
        "-Dio.netty.recycler.maxCapacity=0",
        "-Dio.netty.recycler.maxCapacity.default=0",
        "-DIReallyKnowWhatIAmDoingISwear=true"
    ),
    jvmCommand: String = if (RUNTIME_JVM_COMMAND != null) RUNTIME_JVM_COMMAND!! else "java"
) {
    var identity by ConfigSaver(
        identity
    )
    var cluster by ConfigSaver(
        cluster
    )
    var hostAddress by ConfigSaver(
        hostAddress
    )
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
        fun load(): NetworkNodeConfiguration {
            val configFile = CONFIG_PATH.toFile()
            if (!configFile.exists()) {
                NetworkNodeConfiguration().toConfig("config").save(configFile)
            }
            return ConfigFactory.parseFile(configFile).extract("config")
        }
    }
}