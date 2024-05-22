package org.vivlaniv.nexohub.device

import org.vivlaniv.nexohub.HumiditySensor
import org.vivlaniv.nexohub.PropertyInfo
import org.vivlaniv.nexohub.Schema
import org.vivlaniv.nexohub.TemperatureSensor
import org.vivlaniv.nexohub.Type

class THSensor(
    override var tempSensor: Int = 25,
    override var humidSensor: Int = 40,
) : AbstractDevice(), TemperatureSensor, HumiditySensor {
    override fun getProperties() = listOf(
        PropertyInfo("tempSensor", Schema(Type.TEMPERATURE, isSensor = true), true, tempSensor),
        PropertyInfo("humidSensor", Schema(Type.HUMIDITY, isSensor = true), true, humidSensor),
    )

    override fun toString(): String {
        return "THSensor(tempSensor=$tempSensor, humidSensor=$humidSensor)"
    }
}