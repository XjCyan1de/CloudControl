package com.github.xjcyan1de.cloudcontrol.service

import com.github.xjcyan1de.cloudcontrol.api.service.*
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList
import kotlin.random.Random

class JVMCloudService(val serviceConfiguration: ServiceConfiguration) : CloudService {
    override val runtime: String = "jvm"

    override val templates: List<ServiceTemplate> = ArrayList()
    override val deployments: List<ServiceDeployment> = ArrayList()
    override val waitingTemplates: Queue<ServiceTemplate> = ConcurrentLinkedQueue()
    override val groups: List<ServiceGroup> = ArrayList()
    override val lifeCycle: ServiceLifeCycle = ServiceLifeCycle.DEFINED
    override val id: ServiceId = serviceConfiguration.serviceId
    override val connectionKey: String = Base64.getEncoder().encodeToString(Random.nextBytes(256))
    override var serviceInfoSnapshot: ServiceInfoSnapshot = ServiceInfoSnapshot(serviceConfiguration, lifeCycle)
    override val lastServiceInfoSnapshot: ServiceInfoSnapshot = serviceInfoSnapshot
    override val process: Process? = null
    override val configuredMaxHeapMemory: Int = serviceConfiguration.processConfiguration.maxHeapMemorySize

    override fun runCommand(commandLine: String) {
        TODO("Not yet implemented")
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun restart() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun kill() {
        TODO("Not yet implemented")
    }

    override fun delete() {
        TODO("Not yet implemented")
    }

    override fun isAlive(): Boolean {
        TODO("Not yet implemented")
    }

    override fun includeTemplates() {
        TODO("Not yet implemented")
    }

    override fun deployResources(removeDeployments: Boolean) {
        TODO("Not yet implemented")
    }
}