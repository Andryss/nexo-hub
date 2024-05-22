package org.vivlaniv.nexohub.widgets

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
import org.vivlaniv.nexohub.SavedDevice

@Composable
fun DeviceTabsList(
    devices: List<SavedDevice>,
    onPutProperty: (String, String, Int) -> Unit,
    shouldRefresh: Int
) {
    val rooms = devices.mapNotNull { it.room }.distinct()

    val devicesByRooms = mutableMapOf("All" to devices)
    rooms.forEach { room ->
        devicesByRooms[room] = devices.filter { it.room == room }
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
                rooms.forEachIndexed { index, room ->
                    Tab(
                        selected = (tabIndex == index + 1),
                        onClick = { tabIndex = index + 1 },
                        text = {
                            Text(
                                text = room,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    )
                }
            }
        }
        DeviceCardList(
            devices = (if (tabIndex == 0) devices else devicesByRooms[rooms[tabIndex - 1]]!!),
            onPutProperty = onPutProperty,
            shouldRefresh = shouldRefresh
        )
    }
}