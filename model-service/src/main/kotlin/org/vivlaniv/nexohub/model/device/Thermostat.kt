package org.vivlaniv.nexohub.model.device

import org.vivlaniv.nexohub.model.Heater
import org.vivlaniv.nexohub.common.PropertyInfo
import org.vivlaniv.nexohub.common.Schema
import org.vivlaniv.nexohub.common.Type

class Thermostat(
    override var turn: Int = 0,
    private var mode: TempMode = TempMode.COMFORT,
    override var temperature: Int = mode.temperature,
) : AbstractDevice(), Heater {
    override fun getProperties() = listOf(
        PropertyInfo("turn", Schema(Type.BOOLEAN), false, turn),
        PropertyInfo("mode", Schema(Type.ENUM, enumValues = TempMode.values), false, mode.ordinal),
        PropertyInfo("temperature", Schema(Type.TEMPERATURE, min = 10, max = 40), mode != TempMode.CUSTOM, temperature),
    )

    override fun setProperty(name: String, value: Int) {
        when (name) {
            "turn" -> { turn = value.coerceIn(0, 1) }
            "mode" -> {
                mode = TempMode.entries[value.coerceIn(0, TempMode.values.size)]
                if (mode == TempMode.CUSTOM) return
                temperature = mode.temperature
            }
            "temperature" -> {
                if (mode != TempMode.CUSTOM) return
                temperature = value.coerceIn(10, 40)
            }
            else -> { super.setProperty(name, value) }
        }
    }

    override fun toString(): String {
        return "Thermostat(turn=$turn, mode=$mode, temperature=$temperature)"
    }


    enum class TempMode(val temperature: Int) {
        HEAT(27),
        COMFORT(22),
        COLD(15),
        CUSTOM(-1);
        companion object {
            val values = TempMode.entries.map { it.name }
        }
    }
}