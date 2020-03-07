package com.github.xjcyan1de.cloudcontrol.api.events

abstract class Event {
    companion object
}

fun Event.call(): Boolean {
    //Call
    return if (this is CancelableEvent) {
        !this.isCancelled
    } else {
        false
    }
}