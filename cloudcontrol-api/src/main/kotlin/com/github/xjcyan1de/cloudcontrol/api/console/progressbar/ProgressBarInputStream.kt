package com.github.xjcyan1de.cloudcontrol.api.console.progressbar

import com.github.xjcyan1de.cloudcontrol.api.CloudControl
import com.github.xjcyan1de.cloudcontrol.api.console.Console
import java.io.InputStream
import java.net.URL

class ProgressBarInputStream(
    val wrapped: InputStream,
    val progressBarAnimation: ConsoleProgressBarAnimation
) : InputStream() {
    constructor(wrapped: InputStream, length: Long, console: Console) : this(
        wrapped,
        ConsoleProgressBarAnimation(length)
    ) {
        console.startAnimation(progressBarAnimation)
    }

    override fun read(): Int {
        val read = wrapped.read()
        progressBarAnimation.addValue(1)
        return read
    }

    override fun read(b: ByteArray): Int {
        val read = wrapped.read(b)
        progressBarAnimation.addValue(read)
        return read
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val read = wrapped.read(b, off, len)
        progressBarAnimation.addValue(read)
        return read
    }

    override fun skip(n: Long): Long {
        val length = wrapped.skip(n)
        progressBarAnimation.addValue(length)
        return length
    }

    override fun close() {
        progressBarAnimation.finish()
        wrapped.close()
    }

    companion object {
        fun wrapDownload(url: URL, console: Console = CloudControl.console): InputStream {
            val urlConnection = url.openConnection()
            urlConnection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            )
            urlConnection.connect()
            val inputStream = urlConnection.getInputStream()
            val contentLength = urlConnection.getHeaderFieldLong("Content-Length", inputStream.available().toLong())
            return if (console.isAnimationRunning()) inputStream else ProgressBarInputStream(
                inputStream,
                contentLength,
                console
            )
        }
    }
}