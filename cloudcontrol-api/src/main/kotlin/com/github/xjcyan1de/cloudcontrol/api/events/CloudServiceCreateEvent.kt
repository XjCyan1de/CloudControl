package com.github.xjcyan1de.cloudcontrol.api.events

import com.github.xjcyan1de.cloudcontrol.api.service.configuration.ServiceConfiguration

class CloudServiceCreateEvent(
    val serviceConfiguration: ServiceConfiguration
) : Event(), CancelableEvent {
    override var isCancelled: Boolean = false

    companion object
}