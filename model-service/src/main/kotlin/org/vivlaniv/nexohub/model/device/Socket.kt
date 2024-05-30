package org.vivlaniv.nexohub.model.device

import org.vivlaniv.nexohub.common.PropertyInfo
import org.vivlaniv.nexohub.common.Schema
import org.vivlaniv.nexohub.common.Type

class Socket(
    private var turn: Int = 0
) : AbstractDevice() {
    override fun getProperties() = listOf(
        PropertyInfo("turn", Schema(Type.BOOLEAN), false, turn)
    )

    override fun setProperty(name: String, value: Int) {
        when (name) {
            "turn" -> { turn = value.coerceIn(0, 1) }
            else -> { super.setProperty(name, value) }
        }
    }

    override fun toString(): String {
        return "Socket(turn=$turn)"
    }
}