package com.github.xjcyan1de.cloudcontrol.api.service

import java.util.*

interface CloudService {
    val runtime: String
    val templates: List<ServiceTemplate>
    val deployments: List<ServiceDeployment>
    val waitingTemplates: Queue<ServiceTemplate>
    val groups: List<ServiceGroup>
    val lifeCycle: ServiceLifeCycle
    val id: ServiceId
    val connectionKey: String
    var serviceInfoSnapshot: ServiceInfoSnapshot
    val lastServiceInfoSnapshot: ServiceInfoSnapshot
    val process: Process?
    val configuredMaxHeapMemory: Int

    fun runCommand(commandLine: String)

    fun start()
    fun restart()
    fun stop()
    fun kill()
    fun delete()
    fun isAlive(): Boolean
    fun includeTemplates()
    fun deployResources(removeDeployments: Boolean = true)
}