package org.vivlaniv.nexohub.device

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.vivlaniv.nexohub.DeviceInfo
import org.vivlaniv.nexohub.PropertyInfo
import java.util.UUID

interface Device {
    fun getInfo(): DeviceInfo = DeviceInfo(getId(), getType(), getProperties())
    fun getId(): String
    fun getType(): String = javaClass.simpleName
    fun getProperties(): List<PropertyInfo>
    fun setProperty(name: String, value: Int)
}

abstract class AbstractDevice : Device {
    private val id = UUID.randomUUID().toString()
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun getId(): String = id
    override fun getProperties() = listOf<PropertyInfo>()
    override fun setProperty(name: String, value: Int) = log.warn("unknown property $name for $this")
}