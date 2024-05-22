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
import org.vivlaniv.nexohub.entities.cards.pictures.DefaultCanvas
import org.vivlaniv.nexohub.entities.cards.pictures.DeviceFrame
import org.vivlaniv.nexohub.entities.cards.property.DeviceProperty

@Composable
fun DefaultCard(
    device: SavedDevice,
    onPutProperty: (String, Int) -> Unit,
    shouldRefresh: Int
) {
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
                        DefaultCanvas()
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
            if (device.properties.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(8.dp),
                ) {
                    for (property in device.properties) {
                        DeviceProperty(
                            propertyInfo = property,
                            putPropertyCallback = { onPutProperty(property.name, it) },
                            shouldRefresh = shouldRefresh
                        )
                    }
                }
            }
        }
    }
}