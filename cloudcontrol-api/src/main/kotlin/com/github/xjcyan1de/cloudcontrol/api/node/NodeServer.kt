package com.github.xjcyan1de.cloudcontrol.api.node

import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNode
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNodeInfo
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceInfoSnapshot
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceLifeCycle
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTask
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import java.util.*

interface NodeServer {
    var node: NetworkNode
    var nodeInfo: NetworkNodeInfo

    suspend fun sendCommand(commandLine: String): Array<String>

    suspend fun createCloudService(task: ServiceTask): ServiceInfoSnapshot
    suspend fun createCloudService(serviceConfiguration: ServiceConfiguration): ServiceInfoSnapshot

    suspend fun setCloudServiceLifeCycle(serviceInfoSnapshot: ServiceInfoSnapshot, lifeCycle: ServiceLifeCycle)
    suspend fun restartCloudService(serviceInfoSnapshot: ServiceInfoSnapshot)
    suspend fun killCloudService(serviceInfoSnapshot: ServiceInfoSnapshot)
    suspend fun runCommand(serviceInfoSnapshot: ServiceInfoSnapshot, commandLine: String)
    suspend fun deployResources(uniqueId: UUID, removeDeployments: Boolean = true)
}