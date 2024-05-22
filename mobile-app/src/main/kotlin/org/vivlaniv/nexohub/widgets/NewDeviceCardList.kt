package org.vivlaniv.nexohub.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.vivlaniv.nexohub.DeviceInfo
import org.vivlaniv.nexohub.entities.cards.NewDeviceCard

@Composable
fun NewDeviceCardList(devices: List<DeviceInfo>, onSelectDevice: (DeviceInfo) -> Unit) {
    Column {
        if (devices.isEmpty()) {
            Text(
                text = "No devices found",
                modifier = Modifier.padding(PaddingValues(vertical = 10.dp)),
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            for (device in devices) {
                NewDeviceCard(
                    device = device,
                    onClick = { onSelectDevice(device) }
                )
            }
        }
    }
}