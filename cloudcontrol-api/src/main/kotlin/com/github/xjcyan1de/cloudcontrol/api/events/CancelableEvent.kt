package com.github.xjcyan1de.cloudcontrol.api.events

interface CancelableEvent {
    var isCancelled: Boolean

    companion object
}

