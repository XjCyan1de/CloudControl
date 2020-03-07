package com.github.xjcyan1de.cloudcontrol.api.console

import com.github.xjcyan1de.cloudcontrol.api.command.TabCompleter
import java.util.*
import java.util.function.Consumer

interface Console : AutoCloseable {
    val runningAnimations: Collection<AbstractConsoleAnimation?>?
    fun startAnimation(animation: AbstractConsoleAnimation?)
    fun isAnimationRunning(): Boolean
    fun togglePrinting(enabled: Boolean)
    val isPrintingEnabled: Boolean
    val hasAnimationSupport: Boolean get() = hasColorSupport
    val hasColorSupport: Boolean
    var commandHistory: List<String>
    fun setCommandInputValue(commandInputValue: String)
    suspend fun readLine(): String
    fun enableAllHandlers()
    fun disableAllHandlers()
    fun enableAllTabCompletionHandlers()
    fun disableAllTabCompletionHandlers()
    fun enableAllCommandHandlers()
    fun disableAllCommandHandlers()
    fun addCommandHandler(uniqueId: UUID, inputConsumer: Consumer<String>)
    fun removeCommandHandler(uniqueId: UUID)
    fun addTabCompletionHandler(uniqueId: UUID, completer: TabCompleter)
    fun removeTabCompletionHandler(uniqueId: UUID)
    fun writeRaw(rawText: String): Console
    fun forceWrite(text: String): Console
    fun forceWriteLine(text: String): Console
    fun write(text: String): Console
    fun writeLine(text: String): Console
    var prompt: String
    fun resetPrompt()
    fun clearScreen()
    var screenName: String
}