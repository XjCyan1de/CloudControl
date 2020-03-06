package com.github.xjcyan1de.cloudcontrol.api.service

data class ThreadSnapshot(
    val id: Long,
    val name: String,
    val threadState: Thread.State,
    val isDaemon: Boolean,
    val priority: Int
) {
    constructor(thread: Thread) : this(thread.id, thread.name, thread.state, thread.isDaemon, thread.priority)
}