package org.vivlaniv.nexohub.mobile.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.vivlaniv.nexohub.common.DeviceInfo

@Composable
fun NewDeviceTabsList(devices: List<DeviceInfo>, onSelectDevice: (DeviceInfo) -> Unit) {
    val types = devices.map { it.type }.distinct()

    val devicesByTypes = mutableMapOf("All" to devices)
    types.forEach { type ->
        devicesByTypes[type] = devices.filter { it.type == type }
    }

    var tabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (devices.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = tabIndex,
                edgePadding = 15.dp
            ) {
                Tab(
                    selected = (tabIndex == 0),
                    onClick = { tabIndex = 0 },
                    text = {
                        Text(
                            text = "All",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                )
                types.forEachIndexed { index, type ->
                    Tab(
                        selected = (tabIndex == index + 1),
                        onClick = { tabIndex = index + 1 },
                        text = {
                            Text(
                                text = type,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    )
                }
            }
        }
        NewDeviceCardList(
            devices = (if (tabIndex == 0) devices else devicesByTypes[types[tabIndex - 1]]!!),
            onSelectDevice = onSelectDevice
        )
    }
}