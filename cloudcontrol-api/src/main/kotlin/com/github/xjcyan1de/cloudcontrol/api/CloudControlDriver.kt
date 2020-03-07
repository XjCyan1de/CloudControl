package com.github.xjcyan1de.cloudcontrol.api

import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNodeConfiguration
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNodeProvider
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNodeSnapshot
import com.github.xjcyan1de.cloudcontrol.api.service.CloudServiceWrapper
import com.github.xjcyan1de.cloudcontrol.api.service.GeneralCloudServiceProvider
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceInfoSnapshot
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTaskProvider
import com.github.xjcyan1de.cyanlibz.messenger.Messenger
import java.util.*

interface CloudControlDriver {
    val generalCloudServiceProvider: GeneralCloudServiceProvider
    val serviceTaskProvider: ServiceTaskProvider
    val networkNodeProvider: NetworkNodeProvider

    val messenger: Messenger
    val networkNodeConfiguration: NetworkNodeConfiguration
    val currentNetworkNodeSnapshot: NetworkNodeSnapshot
    val lastNetworkNodeSnapshot: NetworkNodeSnapshot

    fun start()
    fun stop()

    fun getCloudServiceWrapper(name: String): CloudServiceWrapper?
    fun getCloudServiceWrapper(uniqueId: UUID): CloudServiceWrapper?
    fun getCloudServiceWrapper(serviceInfoSnapshot: ServiceInfoSnapshot): CloudServiceWrapper
}