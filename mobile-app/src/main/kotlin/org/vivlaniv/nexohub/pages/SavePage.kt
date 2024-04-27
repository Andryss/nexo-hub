package org.vivlaniv.nexohub.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.vivlaniv.nexohub.AppState
import org.vivlaniv.nexohub.SaveDeviceTask
import org.vivlaniv.nexohub.SaveDeviceTaskResult
import org.vivlaniv.nexohub.TAG
import org.vivlaniv.nexohub.widgets.ReturnTopAppBar

@Composable
fun SavePage(state: AppState, navController: NavController, device: String) {
    val client = state.mqttClient
    val user = state.username

    val room = remember { mutableStateOf(TextFieldValue()) }
    val alias = remember { mutableStateOf(TextFieldValue()) }
    val saveDeviceLoading = remember { mutableStateOf(false) }
    val deviceSaved = remember { mutableStateOf(false) }

    client.subscribe("$user/save/out", 0) { _, message ->
        val response = Json.decodeFromString<SaveDeviceTaskResult>(message.payload.decodeToString())
        if (response.code != 0) {
            Log.i(TAG, "save failed, code ${response.code} message ${response.errorMessage}")
            saveDeviceLoading.value = false
            return@subscribe
        }
        deviceSaved.value = true
        saveDeviceLoading.value = false
        Log.i(TAG, "save performed successfully")
    }

    fun formatTextField(value: TextFieldValue): String? {
        val text = value.text.trim()
        return if (text == "") null else text.lowercase()
    }

    fun sendSaveDevice() {
        Log.i(TAG, "save button tapped")
        saveDeviceLoading.value = true
        val request = SaveDeviceTask(device = device, room = formatTextField(room.value), alias = formatTextField(alias.value))
        client.publish(
            "$user/save/in", Json.encodeToString(request).encodeToByteArray(), 2, false
        )
    }

    Scaffold(
        topBar = {
            ReturnTopAppBar(
                navController = navController,
                title = "Save device",
                showBackIcon = true
            )
        }, content = { padding ->
            if (deviceSaved.value) {
                navController.navigate("home")
            }
            Column(
                modifier = Modifier.padding(padding).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    label = { Text(text = "Device") },
                    value = device,
                    onValueChange = { },
                    enabled = false
                )
                TextField(
                    label = { Text(text = "Room") },
                    value = room.value, 
                    onValueChange = { room.value = it }
                )
                TextField(
                    label = { Text(text = "Alias") },
                    value = alias.value,
                    onValueChange = { alias.value = it }
                )
                Button(
                    onClick = { sendSaveDevice() },
                    enabled = !saveDeviceLoading.value && !deviceSaved.value
                ) {
                    Text(text = "Save")
                }
            }
        }
    )
}