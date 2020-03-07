package com.github.xjcyan1de.cloudcontrol.service

import com.github.xjcyan1de.cloudcontrol.api.service.*
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList
import kotlin.random.Random

class CloudService(
    val serviceConfiguration: ServiceConfiguration
) {
    val templates: List<ServiceTemplate> = ArrayList()
    val deployments: List<ServiceDeployment> = ArrayList()
    val waitingTemplates: Queue<ServiceTemplate> = ConcurrentLinkedQueue()
    val groups: List<ServiceGroup> = ArrayList()
    val lifeCycle: ServiceLifeCycle = ServiceLifeCycle.DEFINED
    val id: ServiceId = serviceConfiguration.serviceId
    val connectionKey: String = Base64.getEncoder().encodeToString(Random.nextBytes(256))
    var serviceInfoSnapshot: ServiceInfoSnapshot = ServiceInfoSnapshot(serviceConfiguration, lifeCycle)
    val lastServiceInfoSnapshot: ServiceInfoSnapshot = serviceInfoSnapshot
    val process: Process? = null
    val configuredMaxHeapMemory: Int = serviceConfiguration.processConfiguration.maxHeapMemorySize

    fun runCommand(commandLine: String) {
        TODO("Not yet implemented")
    }

    fun start() {
        println("Starting service: $serviceConfiguration")
    }

    fun restart() {
        TODO("Not yet implemented")
    }

    fun stop() {
        TODO("Not yet implemented")
    }

    fun kill() {
        TODO("Not yet implemented")
    }

    fun delete() {
        TODO("Not yet implemented")
    }

    fun isAlive(): Boolean {
        TODO("Not yet implemented")
    }

    fun includeTemplates() {
        TODO("Not yet implemented")
    }

    fun deployResources(removeDeployments: Boolean) {
        TODO("Not yet implemented")
    }
}