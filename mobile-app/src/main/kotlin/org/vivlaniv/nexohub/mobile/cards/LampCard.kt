package org.vivlaniv.nexohub.mobile.cards

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
import org.vivlaniv.nexohub.common.SavedDevice
import org.vivlaniv.nexohub.mobile.cards.pictures.DeviceFrame
import org.vivlaniv.nexohub.mobile.cards.pictures.LampCanvas
import org.vivlaniv.nexohub.mobile.cards.property.ColorDeviceProperty
import org.vivlaniv.nexohub.mobile.cards.property.PercentDeviceProperty
import org.vivlaniv.nexohub.mobile.cards.property.TurnDeviceProperty

@Composable
fun LampCard(
    device: SavedDevice,
    onPutProperty: (String, Int) -> Unit,
    shouldRefresh: Int
) {
    val turn = remember(shouldRefresh) {
        device.findProperty("turn")
    }
    val brightness = remember(shouldRefresh) {
        device.findProperty("brightness")
    }
    val color = remember(shouldRefresh) {
        device.findProperty("color")
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
                        LampCanvas(turn.value, brightness.value, color.value)
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
                TurnDeviceProperty(
                    propertyInfo = turn,
                    putPropertyCallback = { onPutProperty(turn.name, it) },
                    shouldRefresh = shouldRefresh
                )
                PercentDeviceProperty(
                    propertyInfo = brightness,
                    putPropertyCallback = { onPutProperty(brightness.name, it) },
                    shouldRefresh = shouldRefresh
                )
                ColorDeviceProperty(
                    propertyInfo = color,
                    putPropertyCallback = { onPutProperty(color.name, it) },
                    shouldRefresh = shouldRefresh
                )
            }
        }
    }
}