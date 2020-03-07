package com.github.xjcyan1de.cloudcontrol.api.console

import org.fusesource.jansi.Ansi
import java.util.*

abstract class AbstractConsoleAnimation : Runnable {
    var console: Console? = null
    var updateInterval = 25
    var startTime: Long = 0
        private set
    var cursor = 1
    var isStaticCursor = false
    private val finishHandler: MutableCollection<Runnable> = ArrayList()
    val timeElapsed: Long
        get() = System.currentTimeMillis() - startTime

    fun addToCursor(cursor: Int) {
        if (!isStaticCursor) {
            this.cursor += cursor
        }
    }

    fun addFinishHandler(finishHandler: Runnable) {
        this.finishHandler.add(finishHandler)
    }

    protected fun print(vararg input: String?) {
        if (input.isEmpty()) {
            return
        }
        val ansi = Ansi
            .ansi()
            .saveCursorPosition()
            .cursorUp(cursor)
            .eraseLine(Ansi.Erase.ALL)
        for (a in input) {
            ansi.a(a)
        }
        console!!.forceWrite(ansi.restoreCursorPosition().toString())
    }

    protected fun eraseLastLine() {
        console!!.writeRaw(
            Ansi.ansi()
                .reset()
                .cursorUp(1)
                .eraseLine()
                .toString()
        )
    }

    protected abstract fun handleTick(): Boolean
    override fun run() {
        startTime = System.currentTimeMillis()
        while (!Thread.interrupted() && !handleTick()) {
            try {
                Thread.sleep(updateInterval.toLong())
            } catch (exception: InterruptedException) {
                exception.printStackTrace()
            }
        }
        for (runnable in finishHandler) {
            runnable.run()
        }
    }
}