package org.vivlaniv.nexohub.model.device

import org.vivlaniv.nexohub.common.PropertyInfo
import org.vivlaniv.nexohub.common.util.RGBToColor
import org.vivlaniv.nexohub.common.Schema
import org.vivlaniv.nexohub.common.Type
import org.vivlaniv.nexohub.common.util.colorMask
import org.vivlaniv.nexohub.common.util.colorToRGB

class Lamp(
    private var turn: Int = 0,
    private var brightness: Int = 80,
    private var color: Int = RGBToColor(255, 255, 255)
) : AbstractDevice() {
    override fun getProperties() = listOf(
        PropertyInfo("turn", Schema(Type.BOOLEAN), false, turn),
        PropertyInfo("brightness", Schema(Type.PERCENT), false, brightness),
        PropertyInfo("color", Schema(Type.COLOR), false, color)
    )

    override fun setProperty(name: String, value: Int) {
        when (name) {
            "turn" -> { turn = value.coerceIn(0, 1) }
            "brightness" -> { brightness = value.coerceIn(0, 100) }
            "color" -> { color = value.and(colorMask) }
            else -> { super.setProperty(name, value) }
        }
    }

    override fun toString(): String {
        val (red, green, blue) = colorToRGB(color)
        return "Lamp(turn=$turn, brightness=$brightness, red=$red, green=$green, blue=$blue)"
    }
}