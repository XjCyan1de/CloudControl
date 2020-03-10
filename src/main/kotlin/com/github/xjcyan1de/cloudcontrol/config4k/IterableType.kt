package com.github.xjcyan1de.cloudcontrol.config4k

import com.typesafe.config.Config
import io.github.config4k.ClassContainer
import io.github.config4k.CustomType
import io.github.config4k.toConfig

object IterableType : CustomType {
    override fun parse(clazz: ClassContainer, config: Config, name: String): Any? =
        config.getList(name)

    override fun testParse(clazz: ClassContainer): Boolean = Iterable::class == clazz.mapperClass

    override fun testToConfig(obj: Any): Boolean = obj.javaClass.isAssignableFrom(Iterable::class.java)

    override fun toConfig(obj: Any, name: String): Config = (obj as Iterable<*>).toList().toConfig(name)
}