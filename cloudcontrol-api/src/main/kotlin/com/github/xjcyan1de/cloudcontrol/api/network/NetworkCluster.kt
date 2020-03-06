package com.github.xjcyan1de.cloudcontrol.api.network

import java.util.*

data class NetworkCluster(
    var uniqueId: UUID,
    var nodes: List<NetworkNode>
) {
    override fun equals(other: Any?): Boolean = uniqueId == (other as? NetworkCluster)?.uniqueId

    override fun hashCode(): Int = uniqueId.hashCode()
}