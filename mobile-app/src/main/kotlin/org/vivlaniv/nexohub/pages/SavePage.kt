package org.vivlaniv.nexohub.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.vivlaniv.nexohub.AppState
import org.vivlaniv.nexohub.DeviceInfo
import org.vivlaniv.nexohub.SaveDeviceTask
import org.vivlaniv.nexohub.SaveDeviceTaskResult
import org.vivlaniv.nexohub.TAG
import org.vivlaniv.nexohub.entities.cards.pictures.DeviceCanvas
import org.vivlaniv.nexohub.entities.cards.pictures.DeviceFrame
import org.vivlaniv.nexohub.util.android.mqtt.publish
import org.vivlaniv.nexohub.util.android.mqtt.subscribe
import org.vivlaniv.nexohub.widgets.TextFieldWithSelection

@Composable
fun SavePage(state: AppState, device: DeviceInfo, onSaved: () -> Unit) {
    val client = state.mqttClient
    val token = state.userToken

    val roomHelpValues = remember { listOf("Bedroom", "Dining room", "Hall",
        "Office", "Kitchen", "Bathroom", "Balcony", "Living room", "Basement") }

    var room by remember { mutableStateOf(TextFieldValue()) }
    var alias by remember { mutableStateOf(TextFieldValue()) }
    var saveDeviceLoading by remember { mutableStateOf(false) }
    var deviceSaved by remember { mutableStateOf(false) }

    var delayedError by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    client.subscribe<SaveDeviceTaskResult>("$token/save/out") { response ->
        if (response.code != 0) {
            Log.i(TAG, "save failed, code ${response.code} message ${response.errorMessage}")
            delayedError = true
            errorText = response.errorMessage ?: "some error occurred"
            return@subscribe
        }
        deviceSaved = true
        saveDeviceLoading = false
        Log.i(TAG, "save performed successfully")
    }

    fun formatTextField(value: TextFieldValue): String? {
        val text = value.text.trim()
        return if (text == "") null else text
    }

    fun sendSaveDevice() {
        Log.i(TAG, "save button tapped")
        isError = false
        saveDeviceLoading = true
        val request = SaveDeviceTask(device = device.id, room = formatTextField(room), alias = formatTextField(alias))
        client.publish("$token/save/in", request)
    }

    LaunchedEffect(key1 = deviceSaved) {
        if (!deviceSaved) return@LaunchedEffect
        onSaved()
    }

    LaunchedEffect(key1 = saveDeviceLoading) {
        if (!saveDeviceLoading) return@LaunchedEffect
        delay(8_000)
        if (!saveDeviceLoading) return@LaunchedEffect
        isError = true
        errorText = "server is not responding, try again later"
        saveDeviceLoading = false
    }

    LaunchedEffect(key1 = delayedError) {
        if (!delayedError) return@LaunchedEffect
        delay(1_500)
        isError = true
        saveDeviceLoading = false
        delayedError = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(10.dp)),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Save new device",
            fontSize = 20.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            DeviceFrame {
                DeviceCanvas(type = device.type)
            }
            Text(
                modifier = Modifier.padding(4.dp),
                text = device.type,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        TextFieldWithSelection(
            options = roomHelpValues,
            onTextChanged = { room = TextFieldValue(it) }
        )
        TextField(
            label = { Text(text = "Alias") },
            value = alias,
            onValueChange = { alias = it }
        )
        if (isError) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
        Button(
            onClick = { sendSaveDevice() },
            modifier = Modifier
                .width(100.dp)
                .height(40.dp),
            enabled = !saveDeviceLoading && !deviceSaved,
            content = {
                if (saveDeviceLoading) {
                    LinearProgressIndicator()
                } else {
                    Text(text = "Save")
                }
            }
        )
    }
}