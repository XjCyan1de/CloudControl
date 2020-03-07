@file:JvmName("Main")

import com.github.xjcyan1de.cloudcontrol.CloudControlNode
import com.github.xjcyan1de.cloudcontrol.api.CloudControl
import com.github.xjcyan1de.cloudcontrol.config4k.UUIDType
import io.github.config4k.registerCustomType

fun main() {
    registerCustomType(UUIDType)
    CloudControl = CloudControlNode
    CloudControl.start()
}