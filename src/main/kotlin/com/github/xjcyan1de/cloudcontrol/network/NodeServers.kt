package com.github.xjcyan1de.cloudcontrol.network

import com.github.xjcyan1de.cloudcontrol.CloudControlNode
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkCluster
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNode
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNodeProvider
import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNodeSnapshot
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTask
import java.util.concurrent.ConcurrentHashMap

class NodeServer(
     var node: NetworkNode
) {
     var nodeSnapshot: NetworkNodeSnapshot? = null
          get() {
               return if (node == CloudControlNode.currentNetworkNodeSnapshot.node) {
                    CloudControlNode.currentNetworkNodeSnapshot
               } else field
          }
}

object NodeServerProvider : NetworkNodeProvider {
     val nodeServersMap = ConcurrentHashMap<String, NodeServer>()

     override suspend fun getNodes(): Collection<NetworkNode> = nodeServersMap.values.map { it.node }

     override suspend fun getNodeInfoSnapshots(): Collection<NetworkNodeSnapshot> =
          nodeServersMap.values.mapNotNull { it.nodeSnapshot }

     override suspend fun sendCommandLine(commandLine: String): Collection<String> {
          TODO("Not yet implemented")
     }

     override suspend fun sendCommandLine(nodeName: String, commandLine: String): Collection<String> {
          TODO("Not yet implemented")
     }

     override suspend fun getNode(name: String): NetworkNode? = nodeServersMap[name]?.node

     override suspend fun getNodeInfoSnapshot(name: String): NetworkNodeSnapshot? = nodeServersMap[name]?.nodeSnapshot

     val nodeServers: Collection<NodeServer> get() = nodeServersMap.values

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
               nodeServersMap.getOrPut(node.name) {
                    NodeServer(node)
               }.node = node
          }

          for (nodeServer in nodeServers) {
               val node = networkCluster.nodes.find { it == nodeServer.node }
               if (node == null) {
                    nodeServersMap.remove(nodeServer.node.name)
               }
          }
     }
}

val nodeServers: Collection<NodeServer> = NodeServerProvider.nodeServers
fun getNodeServer(name: String): NodeServer? = NodeServerProvider.getNodeServer(name)

val ServiceTask.nodeServers: Collection<NodeServer>
     get() = NodeServerProvider.getNodeServers(this)
