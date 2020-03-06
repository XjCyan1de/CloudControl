package com.github.xjcyan1de.cloudcontrol.api.service

import com.github.xjcyan1de.cloudcontrol.api.util.SystemStatistics

data class ProcessSnapshot(
    val heapUsageMemory: Long = SystemStatistics.heapMemoryUsage,
    val noHeapUsageMemory: Long = SystemStatistics.nonHeapMemoryUsage,
    val maxHeapMemory: Long = SystemStatistics.heapMemoryUsage,
    val currentLoadedClassCount: Int = SystemStatistics.loadedClassCount,
    val totalLoadedClassCount: Long = SystemStatistics.totalLoadedClassCount,
    val unloadedClassCount: Long = SystemStatistics.unloadedClassCount,
    val threads: List<ThreadSnapshot> = Thread.getAllStackTraces().keys.map { ThreadSnapshot(it) },
    val cpuUsage: Double = SystemStatistics.processCPUUsage,
    val pid: Int = SystemStatistics.pid
)