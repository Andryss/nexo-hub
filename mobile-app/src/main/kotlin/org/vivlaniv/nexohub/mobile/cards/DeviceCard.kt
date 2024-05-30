package org.vivlaniv.nexohub.mobile.cards

import androidx.compose.runtime.Composable
import org.vivlaniv.nexohub.common.PropertyInfo
import org.vivlaniv.nexohub.common.SavedDevice

@Composable
fun DeviceCard(
    device: SavedDevice,
    onPutProperty: (String, Int) -> Unit,
    shouldRefresh: Int
) {
    when (device.type) {
        "Lamp" -> { LampCard(device, onPutProperty, shouldRefresh) }
        "Teapot" -> { TeapotCard(device, onPutProperty, shouldRefresh) }
        "Socket" -> { SocketCard(device, onPutProperty, shouldRefresh) }
        "Thermostat" -> { ThermostatCard(device, onPutProperty, shouldRefresh) }
        "Humidifier" -> { HumidifierCard(device, onPutProperty, shouldRefresh) }
        "THSensor" -> { THSensorCard(device, onPutProperty, shouldRefresh) }
        else -> { DefaultCard(device, onPutProperty, shouldRefresh) }
    }
}

fun SavedDevice.findProperty(name: String): PropertyInfo =
    properties.find { it.name == name }!!