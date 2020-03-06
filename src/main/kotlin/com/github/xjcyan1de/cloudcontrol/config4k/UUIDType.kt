package com.github.xjcyan1de.cloudcontrol.config4k

import com.typesafe.config.Config
import io.github.config4k.ClassContainer
import io.github.config4k.CustomType
import io.github.config4k.toConfig
import java.util.*

object UUIDType : CustomType {
    override fun parse(clazz: ClassContainer, config: Config, name: String): Any? =
        UUID.fromString(config.getString(name))

    override fun testParse(clazz: ClassContainer): Boolean = clazz.mapperClass == UUID::class

    override fun testToConfig(obj: Any): Boolean = UUID::class.java.isInstance(obj)

    override fun toConfig(obj: Any, name: String): Config = (obj as UUID).toString().toConfig(name)
}