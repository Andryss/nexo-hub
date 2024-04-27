package org.vivlaniv.nexohub.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.vivlaniv.nexohub.AppState
import org.vivlaniv.nexohub.DeviceInfo
import org.vivlaniv.nexohub.SavedDevice
import org.vivlaniv.nexohub.entities.DeviceCard
import org.vivlaniv.nexohub.entities.NewDeviceCard

@Composable
fun DeviceCardList(state: AppState, devices: List<SavedDevice>) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        for (device in devices) {
            DeviceCard(state = state, device = device)
        }
    }
}

@Composable
fun NewDeviceCardList(devices: List<DeviceInfo>, navController: NavController) {
    Column {
        for (device in devices) {
            NewDeviceCard(device = device, navController = navController)
        }
    }
}
