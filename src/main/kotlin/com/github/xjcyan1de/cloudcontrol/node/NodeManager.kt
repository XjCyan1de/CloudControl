package com.github.xjcyan1de.cloudcontrol.node

import com.github.xjcyan1de.cloudcontrol.api.network.NetworkCluster
import com.github.xjcyan1de.cloudcontrol.api.node.NodeServer
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTask
import java.util.concurrent.ConcurrentHashMap

object NodeManager {
    private val nodeServersMap = ConcurrentHashMap<String, NodeServer>()

    val nodeServers: Collection<NodeServer> = nodeServersMap.values

    fun getNodeServer(name: String): NodeServer? = nodeServersMap[name]

    fun getNodeServers(task: ServiceTask): Collection<NodeServer> {
        return if (task.associatedNodes.isEmpty()) {
            nodeServers
        } else {
            nodeServers.filter { task.associatedNodes.contains(it.node) }
        }
    }

    fun setClusterServers(networkCluster: NetworkCluster) {
        for (node in networkCluster.nodes) {
            if (nodeServersMap.containsKey(node.name)) {
                nodeServersMap[node.name]?.node = node
            } else {

            }
        }
    }
}