package com.github.xjcyan1de.cloudcontrol.api

import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNodeInfo
import com.github.xjcyan1de.cloudcontrol.api.node.NodeConfiguration
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceProvider
import com.github.xjcyan1de.cyanlibz.messenger.Messenger

interface CloudControlDriver {
    val serviceProvider: ServiceProvider
    val messenger: Messenger
    val nodeConfiguration: NodeConfiguration
    val currentNetworkNodeInfo: NetworkNodeInfo
    val lastNetworkNodeInfo: NetworkNodeInfo

    fun start()
    fun stop()
}