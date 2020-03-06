package com.github.xjcyan1de.cloudcontrol.api

import com.github.xjcyan1de.cloudcontrol.api.service.ServiceEnvironment
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceInfoSnapshot
import com.github.xjcyan1de.cloudcontrol.api.service.ServiceTask
import com.github.xjcyan1de.cyanlibz.messenger.conversation.ConversationMessage
import com.github.xjcyan1de.cyanlibz.messenger.conversation.ConversationReplyListener
import com.github.xjcyan1de.cyanlibz.messenger.extensions.getConversationChannel
import com.typesafe.config.Config
import com.typesafe.config.ConfigRenderOptions
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import java.io.File
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit

const val TPS = 1000
val USER get() = System.getProperty("user.name")

val LAUNCHER_SELECT_VERSION get() = System.getProperty("cloudcontrol.launcher.select.version", "1.0")
val CONSOLE_PROMT get() = System.getProperty("cloudcontrol.console.prompt", "§b%user%§r@§7%screen% §f=> §r")
val TEMP_DIR get() = Paths.get(System.getProperty("cloudcontrol.tempDir", "temp"))
val LOCAL_DIR get() = Paths.get(System.getProperty("cloudcontrol.tempDir", "local"))
val CONFIG_PATH get() = Paths.get(System.getProperty("cloudcontrol.config.path", "config.conf"))
val GROUPS_CONFIG_PATH = Paths.get(System.getProperty("cloudcontrol.config.groups.path", "local/groups.conf"))
val TASKS_DIRECTORY = Paths.get(System.getProperty("cloudcontrol.config.tasks.directory.path", "local/tasks"))

val CLUSTER_ID: String? get() = System.getenv("CLOUDCONTROL_CLUSTER_ID")
val CLUSTER_NODE_UNIQUE_ID: String? get() = System.getenv("CLOUDCONTROL_CLUSTER_NODE_UNIQUE_ID")
val RUNTIME_JVM_COMMAND: String? get() = System.getenv("CLOUDCONTROL_RUNTIME_JVM_COMMAND")


fun Config.save(file: File) {
    val configRenderOptions = ConfigRenderOptions.defaults().setOriginComments(false).setJson(false)
    root().render(configRenderOptions).also {
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        }
        file.writeText(it)
    }
}

lateinit var cloudControlDriver: CloudControlDriver

val services: Collection<ServiceInfoSnapshot> get() = cloudControlDriver.serviceProvider.services
val runningServices: Collection<ServiceInfoSnapshot> get() = cloudControlDriver.serviceProvider.runningServices

suspend fun getServices(task: ServiceTask): Collection<ServiceInfoSnapshot> =
    cloudControlDriver.serviceProvider.getServices(task)

suspend fun getServices(environment: ServiceEnvironment): Collection<ServiceInfoSnapshot> =
    cloudControlDriver.serviceProvider.getServices(environment)

suspend fun getService(name: String): ServiceInfoSnapshot? = cloudControlDriver.serviceProvider.getService(name)
suspend fun getService(uniqueId: UUID): ServiceInfoSnapshot? = cloudControlDriver.serviceProvider.getService(uniqueId)

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
    val conversationChannel = cloudControlDriver.messenger.getConversationChannel<Packet<T>, Packet<R>>(channel)
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