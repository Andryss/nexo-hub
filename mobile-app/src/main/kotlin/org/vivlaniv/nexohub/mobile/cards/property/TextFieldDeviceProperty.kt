package org.vivlaniv.nexohub.mobile.cards.property

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.vivlaniv.nexohub.common.PropertyInfo

@Composable
fun TextFieldDeviceProperty(
    propertyInfo: PropertyInfo,
    putPropertyCallback: (Int) -> Unit,
    shouldRefresh: Int
) {
    val propValue = remember(shouldRefresh) {
        mutableStateOf(TextFieldValue())
    }

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