package com.github.xjcyan1de.cloudcontrol.api.network

import com.github.xjcyan1de.cloudcontrol.api.CloudControl
import com.github.xjcyan1de.cloudcontrol.api.service.ProcessSnapshot
import kotlinx.coroutines.runBlocking
import java.time.Instant

data class NetworkNode(
    val name: String
) {
    override fun hashCode(): Int = name.hashCode()

    override fun equals(other: Any?): Boolean = name.equals((other as? NetworkNode)?.name, true)

    companion object
}

data class NetworkNodeSnapshot(
    val creationTime: Instant,
    val node: NetworkNode,
    val version: String,
    val currentServicesCount: Int,
    val usedMemory: Int,
    val reservedMemory: Int,
    val maxMemory: Int,
    val processSnapshot: ProcessSnapshot,
    val systemCpuUsage: Double
) {
    companion object
}

interface NetworkNodeProvider {
    suspend fun getNodes(): Collection<NetworkNode>
    suspend fun getNodeInfoSnapshots(): Collection<NetworkNodeSnapshot>

    suspend fun sendCommandLine(commandLine: String): Collection<String>
    suspend fun sendCommandLine(nodeName: String, commandLine: String): Collection<String>

    suspend fun getNode(name: String): NetworkNode?
    suspend fun getNodeInfoSnapshot(name: String): NetworkNodeSnapshot?

    companion object
}

inline val nodes
    get() = runBlocking { CloudControl.networkNodeProvider.getNodes() }
inline val nodeInfoSnapshots
    get() = runBlocking { CloudControl.networkNodeProvider.getNodeInfoSnapshots() }


suspend fun sendCommandLine(commandLine: String) =
    CloudControl.networkNodeProvider.sendCommandLine(commandLine)

suspend fun sendCommandLine(nodeName: String, commandLine: String) =
    CloudControl.networkNodeProvider.sendCommandLine(nodeName, commandLine)


suspend fun getNode(name: String) =
    CloudControl.networkNodeProvider.getNode(name)

suspend fun getNodeInfoSnapshot(name: String) =
    CloudControl.networkNodeProvider.getNodeInfoSnapshot(name)

fun Iterable<NetworkNodeSnapshot?>.findLessLoaded(): NetworkNodeSnapshot? =
    asSequence().maxBy {
        if (it != null) {
            (it.maxMemory - it.reservedMemory) + (100 - it.systemCpuUsage)
        } else {
            -1.0
        }
    }