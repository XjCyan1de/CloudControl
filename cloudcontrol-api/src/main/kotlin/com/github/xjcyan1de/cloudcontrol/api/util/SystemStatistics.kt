package com.github.xjcyan1de.cloudcontrol.api.util

import com.sun.management.OperatingSystemMXBean
import java.lang.management.ManagementFactory
import java.text.DecimalFormat

object SystemStatistics {
    val CPU_USAGE_OUTPUT_FORMAT = DecimalFormat("##.##")
    val systemCPUusage get() = (ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean).systemCpuLoad * 100
    val processCPUUsage get() = (ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean).processCpuLoad * 100
    val systemMemory get() = (ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean).totalPhysicalMemorySize
    val systemLoadAverage get() = ManagementFactory.getOperatingSystemMXBean().systemLoadAverage
    val pid: Int
        get() {
            val runtimeName = ManagementFactory.getRuntimeMXBean().name
            val index = runtimeName.indexOf('@')
            return if (index < 1) -1 else runtimeName.substring(0, index).toInt()
        }
    val heapMemoryUsage get() = ManagementFactory.getMemoryMXBean().heapMemoryUsage.used
    val nonHeapMemoryUsage get() = ManagementFactory.getMemoryMXBean().nonHeapMemoryUsage.used
    val maxHeapMemory get() = ManagementFactory.getMemoryMXBean().nonHeapMemoryUsage.max
    val maxNonHeapMemory get() = ManagementFactory.getMemoryMXBean().nonHeapMemoryUsage.max
    val loadedClassCount get() = ManagementFactory.getClassLoadingMXBean().loadedClassCount
    val totalLoadedClassCount get() = ManagementFactory.getClassLoadingMXBean().totalLoadedClassCount
    val unloadedClassCount get() = ManagementFactory.getClassLoadingMXBean().unloadedClassCount
}