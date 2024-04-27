package org.vivlaniv.nexohub.entities

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.vivlaniv.nexohub.AppState
import org.vivlaniv.nexohub.DeviceInfo
import org.vivlaniv.nexohub.PropertyInfo
import org.vivlaniv.nexohub.PutDevicePropertyTask
import org.vivlaniv.nexohub.R
import org.vivlaniv.nexohub.SavedDevice
import org.vivlaniv.nexohub.SendDeviceSignalTask
import org.vivlaniv.nexohub.SignalInfo
import org.vivlaniv.nexohub.TAG
import org.vivlaniv.nexohub.Type


val typeImage = mapOf(
    "Lamp" to R.drawable.lamp,
    "Teapot" to R.drawable.teapot
)

@Composable
fun DeviceCard(state: AppState, device: SavedDevice) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
                color = Color.Transparent
            ) {
                Image(
                    painter = painterResource(id = typeImage.getOrDefault(device.type, R.drawable.question)),
                    contentDescription = device.type,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Column {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = device.alias ?: device.type,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier.padding(4.dp),
                    text = device.type
                )
            }
        }

        if (device.room != null) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = "Room: ${device.room}"
            )
        }

        if (device.properties.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = "Properties:"
            )

            Column(
                modifier = Modifier.padding(8.dp),
            ) {
                for (property in device.properties) {
                    DeviceProperty(
                        state = state,
                        device = device.id,
                        propertyInfo = property
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (device.signals.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = "Signals:"
            )

            Column(
                modifier = Modifier.padding(8.dp),
            ) {
                for (signal in device.signals) {
                    DeviceSignal(
                        state = state,
                        device = device.id,
                        signalInfo = signal
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceProperty(state: AppState, device: String, propertyInfo: PropertyInfo) {
    val client = state.mqttClient
    val user = state.username

    fun sendPutDeviceProperty(value: Int) {
        Log.i(TAG, "put property ${propertyInfo.name} tapped")
        val request = PutDevicePropertyTask(device = device, property = propertyInfo.name, value = value)
        client.publish(
            "$user/property/in", Json.encodeToString(request).encodeToByteArray(), 2, false
        )
    }

    when (propertyInfo.schema.type) {
        Type.BOOLEAN -> { BooleanDeviceProperty(propertyInfo = propertyInfo, putPropertyCallback = ::sendPutDeviceProperty) }
        Type.PERCENT -> { PercentDeviceProperty(propertyInfo = propertyInfo, putPropertyCallback = ::sendPutDeviceProperty) }
        else -> { TextFieldDeviceProperty(propertyInfo = propertyInfo, putPropertyCallback = ::sendPutDeviceProperty) }
    }
}

@Composable
fun TextFieldDeviceProperty(propertyInfo: PropertyInfo, putPropertyCallback: (Int) -> Unit) {

    val propValue = remember { mutableStateOf(TextFieldValue()) }

    fun formatTextField(field: TextFieldValue): Int? {
        val text = field.text.trim()
        return if (text == "") null else try {
            text.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }

    fun onButtonClicked() {
        val newValue = formatTextField(propValue.value) ?: return
        putPropertyCallback(newValue)
        propValue.value = TextFieldValue()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = propertyInfo.name)
        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = propertyInfo.value.toString())
            if (!propertyInfo.readOnly) {
                TextField(
                    modifier = Modifier.width(80.dp),
                    value = propValue.value,
                    onValueChange = { propValue.value = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onButtonClicked() })
                )
            }
        }
    }
}

@Composable
fun PercentDeviceProperty(propertyInfo: PropertyInfo, putPropertyCallback: (Int) -> Unit) {

    val percent = remember { mutableFloatStateOf(propertyInfo.value.toFloat() / 100) }

    fun onSlideFinished() {
        putPropertyCallback((percent.floatValue * 100).toInt())
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = propertyInfo.name)
        Slider(
            value = percent.floatValue,
            onValueChange = { percent.floatValue = it },
            modifier = Modifier.width(230.dp),
            onValueChangeFinished = { onSlideFinished() },
            colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.inversePrimary),
            enabled = !propertyInfo.readOnly
        )
    }
}

@Composable
fun BooleanDeviceProperty(propertyInfo: PropertyInfo, putPropertyCallback: (Int) -> Unit) {

    val checked = remember { mutableStateOf(propertyInfo.value != 0) }

    fun onSwitchChanged(newValue: Boolean) {
        checked.value = newValue
        putPropertyCallback(if (checked.value) 1 else 0)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = propertyInfo.name)
        Switch(
            checked = checked.value,
            onCheckedChange = { onSwitchChanged(it) },
            enabled = !propertyInfo.readOnly
        )
    }
}

@Composable
fun DeviceSignal(state: AppState, device: String, signalInfo: SignalInfo) {
    val client = state.mqttClient
    val user = state.username

    val argsValue = remember {
        val list = mutableStateListOf<TextFieldValue>()
        signalInfo.args.forEach { _ -> list.add(TextFieldValue()) }
        list
    }

    fun formatTextField(field: TextFieldValue): Int? {
        val text = field.text.trim()
        return if (text == "") null else try {
            text.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }

    fun sendSignalDevice() {
        Log.i(TAG, "signal ${signalInfo.name} tapped")
        val args = mutableListOf<Int>()
        for (field in argsValue) {
            args.add(formatTextField(field) ?: return)
        }
        val request = SendDeviceSignalTask(device = device, signal = signalInfo.name, arguments = args)
        client.publish(
            "$user/signal/in", Json.encodeToString(request).encodeToByteArray(), 2, false
        )
        for (i in argsValue.indices) {
            argsValue[i] = TextFieldValue()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = signalInfo.name)
        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (idx in signalInfo.args.indices) {
                TextField(
                    modifier = Modifier.width(80.dp),
                    value = argsValue[idx],
                    onValueChange = { argsValue[idx] = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Button(onClick = { sendSignalDevice() }) {
                Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun NewDeviceCard(device: DeviceInfo, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = device.type
            )

            Button(onClick = { navController.navigate("save/${device.id}") }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }
    }
}

@Preview
@Composable
fun DeviceCardPreview() {
    TODO()
//    DeviceCard(
//        device = DeviceInfo(
//            id = "kek",
//            type = "omg",
//            properties = listOf(
//                PropertyInfo("lol", readOnly = false, schema = Schema(Type.BOOLEAN), value = 0),
//                PropertyInfo("wtf", readOnly = true, schema = Schema(Type.PERCENT), value = 42)
//            ),
//            signals = listOf(
//                SignalInfo("idk", args = listOf(Schema(Type.BYTE), Schema(Type.BYTE))),
//                SignalInfo("sup", args = listOf(Schema(Type.TEMPERATURE)))
//            )
//        )
//    )
}
