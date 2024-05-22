package org.vivlaniv.nexohub.entities.cards.property

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.mhssn.colorpicker.ColorPickerDialog
import io.mhssn.colorpicker.ColorPickerType
import org.vivlaniv.nexohub.PropertyInfo
import org.vivlaniv.nexohub.util.RGBToColor
import org.vivlaniv.nexohub.util.colorToRGB

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ColorDeviceProperty(
    propertyInfo: PropertyInfo,
    putPropertyCallback: (Int) -> Unit,
    shouldRefresh: Int
) {
    var color by remember(shouldRefresh) {
        mutableStateOf(propertyInfo.run {
            val (red, green, blue) = colorToRGB(value)
            Color(red, green, blue)
        })
    }

    var showDialog by remember { mutableStateOf(false) }

    fun rnd(color: Float) = (color * 255).toInt()

    fun onColorPicked(newValue: Color) {
        color = newValue
        showDialog = false
        putPropertyCallback(RGBToColor(rnd(color.red), rnd(color.green), rnd(color.blue)))
    }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = propertyInfo.name)
            OutlinedButton(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = color),
                content = { }
            )
        }
        ColorPickerDialog(
            show = showDialog,
            type = ColorPickerType.Classic(showAlphaBar = false),
            properties = DialogProperties(),
            onDismissRequest = { showDialog = false },
            onPickedColor = { onColorPicked(it) },
        )
    }
}