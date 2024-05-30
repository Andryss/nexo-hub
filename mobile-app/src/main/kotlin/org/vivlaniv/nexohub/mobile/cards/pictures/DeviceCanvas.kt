package org.vivlaniv.nexohub.mobile.cards.pictures

import androidx.compose.runtime.Composable

@Composable
fun DeviceCanvas(type: String) {
    when (type) {
        "Lamp" -> { LampCanvas() }
        "Teapot" -> { TeapotCanvas() }
        "Socket" -> { SocketCanvas() }
        "Thermostat" -> { ThermostatCanvas() }
        "Humidifier" -> { HumidifierCanvas() }
        "THSensor" -> { THSensorCanvas() }
        else -> { DefaultCanvas() }
    }
}