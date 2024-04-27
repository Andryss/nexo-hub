package org.vivlaniv.nexohub.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.vivlaniv.nexohub.AppState
import org.vivlaniv.nexohub.DeviceInfo
import org.vivlaniv.nexohub.SearchDevicesTask
import org.vivlaniv.nexohub.SearchDevicesTaskResult
import org.vivlaniv.nexohub.TAG
import org.vivlaniv.nexohub.widgets.NewDeviceCardList

@Composable
fun SearchPage(state: AppState, navController: NavController) {
    val client = state.mqttClient
    val user = state.username

    val searchDevices = remember { mutableStateListOf<DeviceInfo>() }
    val searchDevicesLoading = remember { mutableStateOf(false) }

    client.subscribe("$user/search/out", 0) { _, message ->
        val response = Json.decodeFromString<SearchDevicesTaskResult>(message.payload.decodeToString())
        searchDevices.clear()
        searchDevices.addAll(response.devices)
        searchDevicesLoading.value = false
        Log.i(TAG, "search performed, ${response.devices.size} devices found")
    }

    fun sendSearchDevices() {
        Log.i(TAG, "fetch button tapped")
        searchDevicesLoading.value = true
        val request = SearchDevicesTask()
        client.publish(
            "$user/search/in", Json.encodeToString(request).encodeToByteArray(), 2, false
        )
    }

    LaunchedEffect(true) {
        delay(100)
        sendSearchDevices()
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = { sendSearchDevices() }) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            }
        }
        if (searchDevicesLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            NewDeviceCardList(devices = searchDevices, navController = navController)
        }
    }
}

@Preview
@Composable
fun DeviceSearchPagePreview() {
    TODO()
}