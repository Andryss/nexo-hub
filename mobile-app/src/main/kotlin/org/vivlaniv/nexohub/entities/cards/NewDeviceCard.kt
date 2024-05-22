package org.vivlaniv.nexohub.entities.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vivlaniv.nexohub.DeviceInfo
import org.vivlaniv.nexohub.entities.cards.pictures.DeviceCanvas
import org.vivlaniv.nexohub.entities.cards.pictures.DeviceFrame

@Composable
fun NewDeviceCard(device: DeviceInfo, onClick: () -> Unit) {
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
                    .clickable { onClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(6f),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DeviceFrame {
                        DeviceCanvas(type = device.type)
                    }
                    Column {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = device.type,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Icon(
                    modifier = Modifier.weight(1f),
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            }
        }
    }
}