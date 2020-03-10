@file:JvmName("Main")

import com.github.xjcyan1de.cloudcontrol.CloudControlNode
import com.github.xjcyan1de.cloudcontrol.api.CloudControl
import com.github.xjcyan1de.cloudcontrol.config4k.IterableType
import com.github.xjcyan1de.cloudcontrol.config4k.UUIDType
import com.github.xjcyan1de.cyanlibz.localization.LocalizationManager
import io.github.config4k.registerCustomType
import java.util.*

fun main() {
    registerCustomType(UUIDType)
    registerCustomType(IterableType)

    CloudControl = CloudControlNode
    val properties =
        Thread.currentThread().contextClassLoader.getResourceAsStream("lang/${Locale.getDefault()}.properties")
            .use { input ->
                Properties().apply {
                    load(input)
                }
            }
    val dictionary = properties.asSequence().map { it.key.toString() to it.value.toString() }.toMap()

    LocalizationManager.addDictionary(Locale.getDefault(), dictionary)
    CloudControl.start()
}