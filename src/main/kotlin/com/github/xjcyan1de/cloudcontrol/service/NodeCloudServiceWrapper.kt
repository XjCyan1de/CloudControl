package com.github.xjcyan1de.cloudcontrol.service

import com.github.xjcyan1de.cloudcontrol.api.service.*
import java.util.*

class NodeCloudServiceWrapper(
    override val serviceInfoSnapshot: ServiceInfoSnapshot
) : CloudServiceWrapper {
    override suspend fun getCachedLogMessages(): Queue<String> {
        TODO("Not yet implemented")
    }

    override suspend fun addServiceTemplate(serviceTemplate: ServiceTemplate) {
        TODO("Not yet implemented")
    }

    override suspend fun addServiceDeployment(serviceDeployment: ServiceDeployment) {
        TODO("Not yet implemented")
    }

    override suspend fun stop() {
        TODO("Not yet implemented")
    }

    override suspend fun start() {
        TODO("Not yet implemented")
    }

    override suspend fun delete() {
        TODO("Not yet implemented")
    }

    override suspend fun setLifeCycle(lifeCycle: ServiceLifeCycle) {
        TODO("Not yet implemented")
    }

    override suspend fun restart() {
        TODO("Not yet implemented")
    }

    override suspend fun kill() {
        TODO("Not yet implemented")
    }

    override suspend fun runCommand(command: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deployResources(removeDeployments: Boolean) {
        TODO("Not yet implemented")
    }
}