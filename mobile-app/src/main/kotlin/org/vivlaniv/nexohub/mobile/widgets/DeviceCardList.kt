package org.vivlaniv.nexohub.mobile.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.vivlaniv.nexohub.common.SavedDevice
import org.vivlaniv.nexohub.mobile.cards.DeviceCard

@Composable
fun DeviceCardList(
    devices: List<SavedDevice>,
    onPutProperty: (String, String, Int) -> Unit,
    shouldRefresh: Int
) {
    Column {
        if (devices.isEmpty()) {
            Text(
                text = "No devices saved",
                modifier = Modifier.padding(PaddingValues(vertical = 10.dp)),
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            for (device in devices) {
                DeviceCard(
                    device = device,
                    onPutProperty = { p, v -> onPutProperty(device.id, p, v) },
                    shouldRefresh = shouldRefresh
                )
            }
        }
    }
}
