package org.vivlaniv.nexohub.model.device

import org.vivlaniv.nexohub.model.Heatable
import org.vivlaniv.nexohub.common.PropertyInfo
import org.vivlaniv.nexohub.common.Schema
import org.vivlaniv.nexohub.common.Type

class Teapot(
    private var volume: Int = 70,
    override var temperature: Int = 20,
    var mode: TeaMode = TeaMode.NONE,
) : AbstractDevice(), Heatable {
    override fun getProperties() = listOf(
        PropertyInfo("volume", Schema(Type.PERCENT, isSensor = true), true, volume),
        PropertyInfo("temperature", Schema(Type.TEMPERATURE, min = 10, max = 100, isSensor = true), true, temperature),
        PropertyInfo("mode", Schema(Type.ENUM, enumValues = TeaMode.values), false, mode.ordinal),
    )

    override fun setProperty(name: String, value: Int) {
        when (name) {
            "mode" -> {
                mode = TeaMode.entries[value.coerceIn(0, TeaMode.values.size)]
            }
            else -> { super.setProperty(name, value) }
        }
    }


    enum class TeaMode(val temperature: Int) {
        BOIL(100),
        HOLD_HOT(80),
        HOLD_WARM(50),
        NONE(-1);
        companion object {
            val values = TeaMode.entries.map { it.name }
        }
    }
}