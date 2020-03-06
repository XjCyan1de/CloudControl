package com.github.xjcyan1de.cloudcontrol.api.service

import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration

interface CloudServiceFactory {
    suspend fun createCloudService(serviceConfiguration: ServiceConfiguration): ServiceInfoSnapshot
}