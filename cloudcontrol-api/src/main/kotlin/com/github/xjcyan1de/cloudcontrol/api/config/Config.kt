package com.github.xjcyan1de.cloudcontrol.api.config

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Represent
import org.yaml.snakeyaml.representer.Representer
import java.io.File
import java.io.InputStream
import kotlin.reflect.KProperty

class Config(
    map: Map<*, *> = emptyMap<String, Any>(),
    defaults: ConfigSection? = null
) : ConfigSection(map, defaults) {

    open inner class any(val path: String, val def: Any? = null) {
        operator fun getValue(ref: Any?, prop: KProperty<*>) = get(path) ?: def
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Any?) = set(path, value)
    }

    open inner class list(val path: String, val def: List<Any> = emptyList()) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<*> = getList(path).orEmpty()
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Any>) = set(path, value)
    }

    open inner class boolean(val path: String, val def: Boolean = false) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): Boolean = getBoolean(path) ?: def
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Boolean) = set(path, value)
    }

    open inner class booleanList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Boolean> = getBooleanList(path).orEmpty()
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Boolean>) = set(path, value)
    }

    open inner class string(val path: String, val def: String = "") {
        operator fun getValue(ref: Any?, prop: KProperty<*>): String = getString(path) ?: def
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: String) = set(path, value)
    }

    open inner class stringList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<String> = getStringList(path).orEmpty()
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<String>) = set(path, value)
    }

    open inner class int(val path: String, val def: Int = 0) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): Int = getInt(path) ?: def
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Int) = set(path, value)
    }

    open inner class intList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Int> = getIntList(path).orEmpty()
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Int>) = set(path, value)
    }

    open inner class long(val path: String, val def: Long = 0L) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): Long = getLong(path) ?: def
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Long) = set(path, value)
    }

    open inner class longList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Long> = getLongList(path).orEmpty()
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Long>) = set(path, value)
    }

    open inner class double(val path: String, val def: Double = 0.0) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): Double = getDouble(path) ?: def
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Double) = set(path, value)
    }

    open inner class doubleList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Double> = getDoubleList(path).orEmpty()
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Double>) = set(path, value)
    }

    open inner class byteList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Byte> = getByteList(path).orEmpty()
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Byte>) = set(path, value)
    }

    open inner class floatList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Float> = getFloatList(path).orEmpty()
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Float>) = set(path, value)
    }

    open inner class shortList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Short> = getShortList(path).orEmpty()
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Short>) = set(path, value)
    }

    open inner class charList(val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<Char> = getCharList(path).orEmpty()
        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Char>) = set(path, value)
    }


    companion object {
        private val yaml: ThreadLocal<Yaml> = object : ThreadLocal<Yaml>() {
            override fun initialValue(): Yaml? {
                val representer: Representer = object : Representer() {
                    init {
                        representers[ConfigSection::class.java] =
                            Represent { data -> represent((data as ConfigSection).asMap()) }
                    }
                }
                val options = DumperOptions()
                options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                return Yaml(Constructor(), representer, options)
            }
        }

        fun save(configSection: ConfigSection, file: File, type: ConfigType) {
            file.outputStream().use {
                when (type) {
                    ConfigType.YAML -> {
                        yaml.get().dump(configSection.asMap(), it.writer())
                    }
                    else -> TODO()
                }
            }
        }

        fun load(file: File, type: ConfigType): Config = file.inputStream().use {
            load(it, type)
        }

        fun load(inputStream: InputStream, type: ConfigType): Config {
            when (type) {
                ConfigType.YAML -> {
                    val map = yaml.get().loadAs(inputStream, LinkedHashMap::class.java) ?: LinkedHashMap<String, Any>()
                    return Config(map)
                }
                else -> TODO()
            }
        }
    }
}