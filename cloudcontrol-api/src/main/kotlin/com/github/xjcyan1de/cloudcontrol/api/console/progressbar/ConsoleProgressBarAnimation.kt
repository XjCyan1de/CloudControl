package com.github.xjcyan1de.cloudcontrol.api.console.progressbar

import com.github.xjcyan1de.cloudcontrol.api.console.AbstractConsoleAnimation
import kotlin.math.max

class ConsoleProgressBarAnimation(
    val length: Long,
    var currentValue: Long = 0,
    val progressChar: Char = '|',
    val lastProgressChar: Char = '|',
    val emptyChar: Char = '-',
    val prefix: String = "§e%percent% % ",
    val suffix: String = "| %value%/%length% MB (%byps% KB/s) | %time%"
) : AbstractConsoleAnimation() {
    fun addValue(value: Number = 1) {
        currentValue += value.toLong()
    }

    fun finish() {
        currentValue = length
    }

    override fun handleTick(): Boolean {
        if (currentValue < length) {
            doUpdate(currentValue.toDouble() / length.toDouble() * 100.0)
            return false
        }

        doUpdate(100.0)
        return true
    }

    private fun doUpdate(percent: Double) {
        val roundPercent = percent.toInt()
        val chars = CharArray(100)
        for (i in 0 until roundPercent) {
            chars[i] = progressChar
        }
        for (i in roundPercent..99) {
            chars[i] = emptyChar
        }
        chars[max(0, roundPercent - 1)] = lastProgressChar //make sure that we don't try to modify a negative index
        super.print(
            this.format(prefix, percent), String(chars),
            this.format(suffix, percent)
        )
    }

    protected fun formatTime(millis: Long): String {
        val seconds = millis / 1000
        var min = (seconds / 60).toString()
        var sec = (seconds - seconds / 60 * 60).toString()
        if (min.length == 1) {
            min = "0$min"
        }
        if (sec.length == 1) {
            sec = "0$sec"
        }
        return "$min:$sec"
    }

    protected fun format(input: String, percent: Double): String? {
        val millis: Long = System.currentTimeMillis() - startTime
        val time = millis / 1000
        return input.replace("%value%", currentValue.toString())
            .replace("%length%", length.toString())
            .replace("%percent%", String.format("%.2f", percent))
            .replace("%time%", formatTime(millis))
            .replace("%bips%", if (time == 0L) "0" else (currentValue / 1024 * 8 / time).toString()) //bits per second
            .replace("%byps%", if (time == 0L) "0" else (currentValue / 1024 / time).toString()) //bytes per second
    }
}