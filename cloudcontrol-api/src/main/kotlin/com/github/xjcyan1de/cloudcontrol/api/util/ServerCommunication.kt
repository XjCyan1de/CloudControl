package com.github.xjcyan1de.cloudcontrol.api.util

import com.github.xjcyan1de.cloudcontrol.api.CloudControl
import com.github.xjcyan1de.cyanlibz.messenger.conversation.ConversationMessage
import com.github.xjcyan1de.cyanlibz.messenger.conversation.ConversationReplyListener
import com.github.xjcyan1de.cyanlibz.messenger.extensions.getConversationChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import java.util.*
import java.util.concurrent.TimeUnit

class Packet<T>(val obj: T) : ConversationMessage {
    override val uuid: UUID = UUID.randomUUID()
}

suspend inline fun <reified T, reified R> sendMessage(
    channel: String,
    message: T,
    duration: Long = 5,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): R {
    val packetMessage = Packet(message)
    val conversationChannel = CloudControl.messenger.getConversationChannel<Packet<T>, Packet<R>>(channel)
    val coroutineChannel = Channel<R>()
    val listener = object : ConversationReplyListener<Packet<R>> {
        override fun onReply(reply: Packet<R>): ConversationReplyListener.RegistrationAction {
            coroutineChannel.sendBlocking(reply.obj)
            return ConversationReplyListener.RegistrationAction.CONTINUE_LISTENING
        }

        override fun onTimeout(replies: List<Packet<R>>) {
            coroutineChannel.close()
        }
    }

    conversationChannel.sendMessage(packetMessage, duration, timeUnit, listener)
    return coroutineChannel.receive()
}
