package com.github.xjcyan1de.cloudcontrol.node

import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNode
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNodeInfo
import com.github.xjcyan1de.cloudcontrol.api.node.NodeServer
import com.github.xjcyan1de.cloudcontrol.api.sendMessage
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceInfoSnapshot
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceLifeCycle
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTask
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import java.util.*

class BaseNodeServer(
    override var node: NetworkNode
) : NodeServer {

    override var nodeInfo: NetworkNodeInfo
        get() = TODO("Not yet implemented")
        set(value) {}

    override suspend fun sendCommand(commandLine: String): Array<String> {
        TODO("Not yet implemented")
    }

    override suspend fun createCloudService(task: ServiceTask): ServiceInfoSnapshot {
        sendMessage<ServiceTask, ServiceInfoSnapshot>("cloud_service.create.service_task", task)
        TODO("Not yet implemented")
    }

    override suspend fun createCloudService(serviceConfiguration: ServiceConfiguration): ServiceInfoSnapshot {
        TODO("Not yet implemented")
    }

    override suspend fun setCloudServiceLifeCycle(
        serviceInfoSnapshot: ServiceInfoSnapshot,
        lifeCycle: ServiceLifeCycle
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun restartCloudService(serviceInfoSnapshot: ServiceInfoSnapshot) {
        TODO("Not yet implemented")
    }

    override suspend fun killCloudService(serviceInfoSnapshot: ServiceInfoSnapshot) {
        TODO("Not yet implemented")
    }

    override suspend fun runCommand(serviceInfoSnapshot: ServiceInfoSnapshot, commandLine: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deployResources(uniqueId: UUID, removeDeployments: Boolean) {
        TODO("Not yet implemented")
    }
}