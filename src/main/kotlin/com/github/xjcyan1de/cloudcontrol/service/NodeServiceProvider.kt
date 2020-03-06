package com.github.xjcyan1de.cloudcontrol.service

import com.github.xjcyan1de.cloudcontrol.CloudControlNode
import com.github.xjcyan1de.cloudcontrol.api.service.*
import java.util.*

object NodeServiceProvider : ServiceProvider {
    override val services: Collection<ServiceInfoSnapshot>
        get() = CloudControlNode.serviceManager.serviceInfoSnapshots
    override val runningServices: Collection<ServiceInfoSnapshot>
        get() = services.filter { it.lifeCycle == ServiceLifeCycle.RUNNING }
    override val currentUsedHeapMemory: Int
        get() = CloudControlNode.serviceManager.currentUsedHeapMemory
    override val currentReservedMemory: Int
        get() = CloudControlNode.serviceManager.currentReservedMemory

    override suspend fun getServices(task: ServiceTask): Collection<ServiceInfoSnapshot> =
        CloudControlNode.serviceManager.getServiceInfoSnapshots(task)

    override suspend fun getServices(environment: ServiceEnvironment): Collection<ServiceInfoSnapshot> =
        CloudControlNode.serviceManager.getServiceInfoSnapshots(environment)

    override suspend fun getService(name: String): ServiceInfoSnapshot? =
        CloudControlNode.serviceManager.getServiceInfoSnapshot(name)

    override suspend fun getService(uniqueId: UUID): ServiceInfoSnapshot? =
        CloudControlNode.serviceManager.getServiceInfoSnapshot(uniqueId)
}