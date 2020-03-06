package com.github.xjcyan1de.cloudcontrol.api.service

import com.github.xjcyan1de.cloudcontrol.api.network.NetworkNode
import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ProcessConfiguration

data class ServiceTask(
    val name: String,
    val runtime: String,
    val maintenance: Boolean,
    val static: Boolean,
    val associatedNodes: List<NetworkNode>,
    val groups: List<String>,
    val processConfiguration: ProcessConfiguration,
    val startPort: Int,
    val minServiceCount: Int,
    val templates: List<ServiceTemplate>,
    val deployments: List<ServiceDeployment>
) {
    private var serviceStartAbilityTime = -1L

    fun forbidServiceStarting(time: Long) {
        serviceStartAbilityTime = System.currentTimeMillis() + time
    }

    fun canStartServices(): Boolean = !maintenance && System.currentTimeMillis() >= serviceStartAbilityTime

    override fun equals(other: Any?): Boolean = name.equals((other as? ServiceTask)?.name, true)

    override fun hashCode(): Int = name.hashCode()
}
