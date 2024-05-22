package org.vivlaniv.nexohub.device

import org.vivlaniv.nexohub.PropertyInfo
import org.vivlaniv.nexohub.Schema
import org.vivlaniv.nexohub.Type
import org.vivlaniv.nexohub.Wettor

class Humidifier(
    override var turn: Int = 0,
    private var mode: HumidMode = HumidMode.COMFORT,
    override var humidity: Int = mode.humidity,
) : AbstractDevice(), Wettor {
    override fun getProperties() = listOf(
        PropertyInfo("turn", Schema(Type.BOOLEAN), false, turn),
        PropertyInfo("mode", Schema(Type.ENUM, enumValues = HumidMode.values), false, mode.ordinal),
        PropertyInfo("humidity", Schema(Type.HUMIDITY), mode != HumidMode.CUSTOM, humidity),
    )

    override fun setProperty(name: String, value: Int) {
        when (name) {
            "turn" -> { turn = value.coerceIn(0, 1) }
            "mode" -> {
                mode = HumidMode.entries[value.coerceIn(0, HumidMode.values.size)]
                if (mode == HumidMode.CUSTOM) return
                humidity = mode.humidity
            }
            "humidity" -> {
                if (mode != HumidMode.CUSTOM) return
                humidity = value.coerceIn(10, 80)
            }
            else -> { super.setProperty(name, value) }
        }
    }

    override fun toString(): String {
        return "Humidifier(turn=$turn, mode=$mode, humidity=$humidity)"
    }


    enum class HumidMode(val humidity: Int) {
        WET(70),
        COMFORT(50),
        DRY(30),
        CUSTOM(-1);
        companion object {
            val values = HumidMode.entries.map { it.name }
        }
    }
}