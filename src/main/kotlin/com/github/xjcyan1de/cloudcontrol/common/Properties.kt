package com.github.xjcyan1de.cloudcontrol.common

class Properties : LinkedHashMap<String, String>() {
    fun getBoolean(key: String): Boolean {
        if (!containsKey(key)) {
            return false
        }
        return get(key)!!.toBoolean()
    }

    companion object {
        fun parseLine(line: String): Properties = parseLine(line.split(" ").toTypedArray())

        fun parseLine(args: Array<String>): Properties {
            val properties = Properties()
            for (argument in args) {
                if (argument.isEmpty() || argument == " ") {
                    continue
                }
                if (argument.contains("=")) {
                    val x = argument.indexOf("=")
                    properties[argument.substring(0, x).replaceFirst("-".toRegex(), "")
                        .replaceFirst("-".toRegex(), "")] =
                        argument.substring(x + 1)
                    continue
                }
                if (argument.contains("--") || argument.contains("-")) {
                    properties[argument.replaceFirst("-".toRegex(), "").replaceFirst("-".toRegex(), "")] = "true"
                    continue
                }
                properties[argument] = "true"
            }
            return properties
        }
    }
}