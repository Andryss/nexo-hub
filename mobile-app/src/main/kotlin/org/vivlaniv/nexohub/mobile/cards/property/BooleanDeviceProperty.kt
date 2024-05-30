package org.vivlaniv.nexohub.mobile.cards.property

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.vivlaniv.nexohub.common.PropertyInfo

@Composable
fun BooleanDeviceProperty(
    propertyInfo: PropertyInfo,
    putPropertyCallback: (Int) -> Unit,
    shouldRefresh: Int
) {
    val checked = remember(shouldRefresh) {
        mutableStateOf(propertyInfo.value != 0)
    }

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