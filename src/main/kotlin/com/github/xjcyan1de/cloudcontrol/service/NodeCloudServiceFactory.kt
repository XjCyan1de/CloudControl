package com.github.xjcyan1de.cloudcontrol.service

import com.github.xjcyan1de.cloudcontrol.api.service.CloudServiceFactory
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceInfoSnapshot
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration

object NodeCloudServiceFactory : CloudServiceFactory {
    override suspend fun createCloudService(serviceConfiguration: ServiceConfiguration): ServiceInfoSnapshot {
        TODO("Not yet implemented")
    }
}