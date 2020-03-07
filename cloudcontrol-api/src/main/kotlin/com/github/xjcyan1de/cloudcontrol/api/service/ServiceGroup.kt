package com.github.xjcyan1de.cloudcontrol.api.service

data class ServiceGroup(
    val name: String,
    val templates: List<ServiceTemplate>,
    val deployments: List<ServiceDeployment>
) {
    override fun hashCode(): Int = name.hashCode()

    override fun equals(other: Any?): Boolean = name.equals((other as? ServiceGroup)?.name, true)

    companion object
}