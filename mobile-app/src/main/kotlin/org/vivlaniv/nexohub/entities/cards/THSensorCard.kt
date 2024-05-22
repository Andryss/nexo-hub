package org.vivlaniv.nexohub.entities.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vivlaniv.nexohub.SavedDevice
import org.vivlaniv.nexohub.entities.cards.pictures.DeviceFrame
import org.vivlaniv.nexohub.entities.cards.pictures.THSensorCanvas
import org.vivlaniv.nexohub.entities.cards.property.HumidityDeviceProperty
import org.vivlaniv.nexohub.entities.cards.property.TemperatureDeviceProperty

@Composable
fun THSensorCard(
    device: SavedDevice,
    onPutProperty: (String, Int) -> Unit,
    shouldRefresh: Int
) {
    val tempSensor = remember(shouldRefresh) {
        device.findProperty("tempSensor")
    }
    val humidSensor = remember(shouldRefresh) {
        device.findProperty("humidSensor")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.inversePrimary
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(6f),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DeviceFrame {
                        THSensorCanvas()
                    }
                    Column {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = device.alias ?: device.type,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (device.room != null) {
                            Text(
                                modifier = Modifier.padding(4.dp),
                                text = device.room!!
                            )
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.45f)
            ) {
                TemperatureDeviceProperty(
                    propertyInfo = tempSensor,
                    putPropertyCallback = { onPutProperty(tempSensor.name, it) },
                    shouldRefresh = shouldRefresh
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(0.45f / 0.55f)
            ) {
                HumidityDeviceProperty(
                    propertyInfo = humidSensor,
                    putPropertyCallback = { onPutProperty(humidSensor.name, it) },
                    shouldRefresh = shouldRefresh
                )
            }
        }
    }
}