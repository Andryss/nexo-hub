package org.vivlaniv.nexohub.device

import org.vivlaniv.nexohub.PropertyInfo
import org.vivlaniv.nexohub.util.RGBToColor
import org.vivlaniv.nexohub.Schema
import org.vivlaniv.nexohub.Type
import org.vivlaniv.nexohub.util.colorMask
import org.vivlaniv.nexohub.util.colorToRGB

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