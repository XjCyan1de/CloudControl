package com.github.xjcyan1de.cloudcontrol

import com.github.xjcyan1de.cloudcontrol.api.console.Console
import com.github.xjcyan1de.cloudcontrol.console.animation.question.QuestionListEntry
import com.github.xjcyan1de.cloudcontrol.console.animation.question.answer.QuestionAnswerTypeString
import com.github.xjcyan1de.cyanlibz.localization.textOf
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.ArrayList

class DefaultInstallation {
    private fun detectAllIPAddresses(): List<String> {
        val resultAddresses = LinkedList<String>()
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val networkInterface = interfaces.nextElement()
            val addresses = networkInterface.inetAddresses
            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement().hostAddress
                if (!resultAddresses.contains(address)) {
                    resultAddresses.add(address)
                }
            }
        }
        return resultAddresses
    }

    private fun detectPreferredIP(internalIPs: List<String>): String = try {
        InetAddress.getLocalHost().hostAddress
    } catch (exception: Exception) {
        internalIPs.first()
    }

    fun executeFirstStartSetup(console: Console, configFileAvailable: Boolean) {
        val internalIPs = ArrayList<String>()
        internalIPs.addAll(detectAllIPAddresses())
        internalIPs.add("127.0.0.1")
        internalIPs.add("127.0.1.1")

        val preferredIP = detectPreferredIP(internalIPs)

        val entries = LinkedList<QuestionListEntry<*>>()
        if (!configFileAvailable) {
            entries.add(QuestionListEntry(
                "nodeId",
                textOf("cloudcontrol.init.setup_node_id").get(),
                object : QuestionAnswerTypeString() {
                    override val recommendation: String = "Node-1"
                }
            ))
        }
    }

}