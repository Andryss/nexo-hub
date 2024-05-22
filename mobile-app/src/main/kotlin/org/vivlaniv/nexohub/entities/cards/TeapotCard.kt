package org.vivlaniv.nexohub.entities.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vivlaniv.nexohub.SavedDevice
import org.vivlaniv.nexohub.entities.cards.pictures.DeviceFrame
import org.vivlaniv.nexohub.entities.cards.pictures.TeapotCanvas
import org.vivlaniv.nexohub.entities.cards.property.EnumDeviceProperty
import org.vivlaniv.nexohub.entities.cards.property.PercentDeviceProperty
import org.vivlaniv.nexohub.entities.cards.property.TemperatureDeviceProperty

@Composable
fun TeapotCard(
    device: SavedDevice,
    onPutProperty: (String, Int) -> Unit,
    shouldRefresh: Int
) {
    val volume = remember(shouldRefresh) {
        device.findProperty("volume")
    }
    val temperature = remember(shouldRefresh) {
        device.findProperty("temperature")
    }
    val mode = remember(shouldRefresh) {
        device.findProperty("mode")
    }

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.inversePrimary
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(6f),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DeviceFrame {
                        TeapotCanvas(volume.value, temperature.value)
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
                Icon(
                    modifier = Modifier.weight(1f),
                    imageVector = Icons.Filled.run { if (expanded) KeyboardArrowUp else KeyboardArrowDown },
                    contentDescription = "expand"
                )
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
            ) {
                PercentDeviceProperty(
                    propertyInfo = volume,
                    putPropertyCallback = { onPutProperty(volume.name, it) },
                    shouldRefresh = shouldRefresh
                )
                TemperatureDeviceProperty(
                    propertyInfo = temperature,
                    putPropertyCallback = { onPutProperty(temperature.name, it) },
                    shouldRefresh = shouldRefresh
                )
                EnumDeviceProperty(
                    propertyInfo = mode,
                    putPropertyCallback = { onPutProperty(mode.name, it) },
                    shouldRefresh = shouldRefresh
                )
            }
        }
    }
}