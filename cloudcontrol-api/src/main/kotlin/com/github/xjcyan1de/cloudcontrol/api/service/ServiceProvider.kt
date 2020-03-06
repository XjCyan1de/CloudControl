package com.github.xjcyan1de.cloudcontrol.api.service

import java.util.*

interface ServiceProvider {
    val services: Collection<ServiceInfoSnapshot>
    val runningServices: Collection<ServiceInfoSnapshot>
    val currentUsedHeapMemory: Int
    val currentReservedMemory: Int

    suspend fun getServices(task: ServiceTask): Collection<ServiceInfoSnapshot>
    suspend fun getServices(environment: ServiceEnvironment): Collection<ServiceInfoSnapshot>

    suspend fun getService(name: String): ServiceInfoSnapshot?
    suspend fun getService(uniqueId: UUID): ServiceInfoSnapshot?
}