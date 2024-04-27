package org.vivlaniv.nexohub.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.vivlaniv.nexohub.AppState
import org.vivlaniv.nexohub.FetchDevicesPropertiesTask
import org.vivlaniv.nexohub.FetchDevicesPropertiesTaskResult
import org.vivlaniv.nexohub.FetchSavedDevicesTask
import org.vivlaniv.nexohub.FetchSavedDevicesTaskResult
import org.vivlaniv.nexohub.PutDevicePropertyTaskResult
import org.vivlaniv.nexohub.SavedDevice
import org.vivlaniv.nexohub.TAG
import org.vivlaniv.nexohub.widgets.DeviceCardList

@Composable
fun HomePage(state: AppState) {
    val client = state.mqttClient
    val user = state.username

    val fetchDevices = remember { mutableStateListOf<SavedDevice>() }
    val autoFetch = remember { mutableStateOf(false) }
    val fetchDevicesLoading = remember { mutableStateOf(false) }
    val handledFetchPropertiesCount = remember { mutableIntStateOf(0) }

    client.subscribe("$user/fetch/devices/out", 0) { _, message ->
        val response = Json.decodeFromString<FetchSavedDevicesTaskResult>(message.payload.decodeToString())
        if (response.code != 0) {
            Log.e(TAG, "fetch saved failed, code ${response.code} message ${response.errorMessage}")
            fetchDevicesLoading.value = false
            return@subscribe
        }
        fetchDevices.clear()
        fetchDevices.addAll(response.devices!!)
        fetchDevicesLoading.value = false
        Log.i(TAG, "fetch saved performed, ${response.devices!!.size} devices found")
    }

    fun sendFetchSavedDevices() {
        Log.i(TAG, "fetch saved button tapped")
        fetchDevicesLoading.value = true
        val request = FetchSavedDevicesTask()
        client.publish(
            "$user/fetch/devices/in", Json.encodeToString(request).encodeToByteArray(), 2, false
        )
    }

    client.subscribe("$user/property/out", 0) { _, message ->
        val response = Json.decodeFromString<PutDevicePropertyTaskResult>(message.payload.decodeToString())
        if (response.code != 0) {
            Log.e(TAG, "put property failed, code ${response.code} message ${response.errorMessage}")
            return@subscribe
        }
        Log.i(TAG, "property of ${response.device} changed successfully")
        val request = FetchDevicesPropertiesTask(include = listOf(response.device))
        client.publish(
            "$user/fetch/props/in", Json.encodeToString(request).encodeToByteArray(), 2, false
        )
    }

    client.subscribe("$user/fetch/props/out", 0) { _, message ->
        val response = Json.decodeFromString<FetchDevicesPropertiesTaskResult>(message.payload.decodeToString())
        if (response.code != 0) {
            Log.e(TAG, "fetch all failed, code ${response.code} message ${response.errorMessage}")
            return@subscribe
        }
        for (device in fetchDevices) {
            val newProperties = response.properties!![device.id] ?: continue
            device.properties = newProperties
        }
        Log.i(TAG, "fetch all performed, ${response.properties!!.size} devices found")
        handledFetchPropertiesCount.intValue++
    }

    fun sendFetchDevicesProperties() {
        Log.i(TAG, "fetch all properties tapped")
        val request = FetchDevicesPropertiesTask()
        client.publish(
            "$user/fetch/props/in", Json.encodeToString(request).encodeToByteArray(), 2, false
        )
    }

    LaunchedEffect(true) {
        delay(100)
        sendFetchSavedDevices()
    }

    LaunchedEffect(key1 = autoFetch.value, key2 = handledFetchPropertiesCount.intValue) {
        if (!autoFetch.value) return@LaunchedEffect
        delay(5000)
        sendFetchDevicesProperties()
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = { sendFetchSavedDevices() }) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
            }
            Button(onClick = { sendFetchDevicesProperties() }) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
            }
            Checkbox(checked = autoFetch.value, onCheckedChange = { autoFetch.value = it })
        }
        if (fetchDevicesLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            DeviceCardList(state = state, devices = fetchDevices)
        }
    }
}