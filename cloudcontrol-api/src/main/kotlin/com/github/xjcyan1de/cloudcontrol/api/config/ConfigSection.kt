package com.github.xjcyan1de.cloudcontrol.api.config

import java.io.File

open class ConfigSection(
    map: Map<*, *> = emptyMap<String, Any>(),
    private val defaults: ConfigSection? = null
) {
    private val self = LinkedHashMap<String, Any>()

    init {
        for (entry in map) {
            val key = if (entry.key == null) "null" else entry.key.toString()
            val value = entry.value
            if (value is Map<*, *>) {
                self[key] = ConfigSection(value, defaults?.getSection(key))
            } else {
                if (value != null) {
                    self[key] = value
                }
            }
        }
    }

    fun asMap(): MutableMap<String, Any> = self

    fun getSectionFor(path: String): ConfigSection {
        val index = path.indexOf(SEPARATOR)
        if (index == -1) {
            return this
        }
        val root = path.substring(0, index)
        var section = self[root]
        if (section == null) {
            section = ConfigSection(defaults = defaults?.getSection(root))
            self[root] = section
        }
        return section as ConfigSection
    }

    private fun getChild(path: String): String {
        val index: Int = path.indexOf(SEPARATOR)
        return if (index == -1) path else path.substring(index + 1)
    }

    @Suppress("SENSELESS_COMPARISON")
    operator fun contains(path: String): Boolean = get(path, null) != null

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(path: String, def: T?): T? {
        val section = getSectionFor(path)
        val value = if (section == this) {
            self[path]
        } else {
            section[getChild(path), def]
        }

        if (value == null && def is ConfigSection) {
            self[path] = def
        }

        return if (value != null) value as T else def
    }

    operator fun get(path: String) = get(path, getDefault(path))

    fun getDefault(path: String): Any? = if (defaults == null) null else defaults[path]

    operator fun set(path: String, value: Any?) {
        val v = if (value is Map<*, *>) {
            ConfigSection(value, defaults?.getSection(path))
        } else value

        val section = getSectionFor(path)
        if (section == this) {
            if (v == null) {
                self.remove(path)
            } else {
                self[path] = v
            }
        } else {
            section[getChild(path)] = v
        }
    }

    fun getSection(path: String): ConfigSection {
        val def = getDefault(path)
        val configSection = if (def is ConfigSection) {
            def
        } else {
            ConfigSection(defaults = defaults?.getSection(path))
        }
        return get(path, configSection)!!
    }


    open val sections: List<ConfigSection>
        get() = self.keys.filterIsInstance<ConfigSection>()

    fun save(file: File, configType: ConfigType) = Config.save(this, file, configType)

    fun getList(path: String): List<*>? = get(path) as? List<*>

    fun getBoolean(path: String): Boolean? = get(path) as? Boolean

    fun getBooleanList(path: String): List<Boolean>? = getList(path)?.map { it as Boolean }

    fun getString(path: String): String? = get(path).toString()

    fun getStringList(path: String): List<String>? = getList(path)?.map { it.toString() }

    fun getInt(path: String): Int? = get(path)?.let {
        if (it is Number) {
            it.toInt()
        } else try {
            it.toString().toInt()
        } catch (ignored: Exception) {
            null
        }
    }

    fun getIntList(path: String): List<Int>? = getList(path)?.map {
        if (it is Number) {
            it.toInt()
        } else try {
            it.toString().toInt()
        } catch (ignored: Exception) {
            0
        }
    }

    fun getLong(path: String): Long? = get(path)?.let {
        if (it is Number) {
            it.toLong()
        } else try {
            it.toString().toLong()
        } catch (ignored: Exception) {
            null
        }
    }

    fun getLongList(path: String): List<Long>? = getList(path)?.map {
        if (it is Number) {
            it.toLong()
        } else try {
            it.toString().toLong()
        } catch (ignored: Exception) {
            0L
        }
    }

    fun getDouble(path: String): Double? = get(path)?.let {
        if (it is Number) {
            it.toDouble()
        } else try {
            it.toString().toDouble()
        } catch (ignored: Exception) {
            null
        }
    }

    fun getDoubleList(path: String): List<Double>? = getList(path)?.map {
        if (it is Number) {
            it.toDouble()
        } else try {
            it.toString().toDouble()
        } catch (ignored: Exception) {
            0.0
        }
    }

    fun getFloat(path: String): Float? = get(path)?.let {
        if (it is Number) {
            it.toFloat()
        } else try {
            it.toString().toFloat()
        } catch (ignored: Exception) {
            null
        }
    }

    fun getFloatList(path: String): List<Float>? = getList(path)?.map {
        if (it is Number) {
            it.toFloat()
        } else try {
            it.toString().toFloat()
        } catch (ignored: Exception) {
            0.0f
        }
    }

    fun getByte(path: String): Byte? = get(path)?.let {
        if (it is Number) {
            it.toByte()
        } else try {
            it.toString().toByte()
        } catch (ignored: Exception) {
            null
        }
    }

    fun getByteList(path: String): List<Byte>? = getList(path)?.map {
        if (it is Number) {
            it.toByte()
        } else try {
            it.toString().toByte()
        } catch (ignored: Exception) {
            0.toByte()
        }
    }

    fun getChar(path: String): Char? = get(path)?.let {
        if (it is Char) {
            it
        } else {
            it.toString()[0]
        }
    }

    fun getCharList(path: String): List<Char>? = getList(path)?.map {
        if (it is Char) {
            it
        } else {
            it.toString()[0]
        }
    }

    fun getShort(path: String): Short? = get(path)?.let {
        if (it is Number) {
            it.toShort()
        } else try {
            it.toString().toShort()
        } catch (ignored: Exception) {
            null
        }
    }

    fun getShortList(path: String): List<Short>? = getList(path)?.map {
        if (it is Number) {
            it.toShort()
        } else try {
            it.toString().toShort()
        } catch (ignored: Exception) {
            0.toShort()
        }
    }

    override fun toString(): String = self.toString()

    companion object {
        val SEPARATOR = "."
    }
}