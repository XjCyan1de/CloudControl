@file:JvmName("Main")

import com.github.xjcyan1de.cloudcontrol.CloudControlNode
import com.github.xjcyan1de.cloudcontrol.api.cloudControlDriver
import com.github.xjcyan1de.cloudcontrol.config4k.UUIDType
import io.github.config4k.registerCustomType

fun main() {
    registerCustomType(UUIDType)

    cloudControlDriver = CloudControlNode
    cloudControlDriver.start()
}