package org.vivlaniv.nexohub.entities.cards.property

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.vivlaniv.nexohub.PropertyInfo

@Composable
fun TurnDeviceProperty(
    propertyInfo: PropertyInfo,
    putPropertyCallback: (Int) -> Unit,
    shouldRefresh: Int
) {
    var turnedOn by remember(shouldRefresh) {
        mutableStateOf(propertyInfo.value != 0)
    }

    fun onSwitchChanged() {
        turnedOn = !turnedOn
        putPropertyCallback(if (turnedOn) 1 else 0)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ElevatedButton(
            onClick = { onSwitchChanged() },
            modifier = Modifier.fillMaxWidth(),
            content = {
                Icon(
                    imageVector = Icons.Filled.PowerSettingsNew,
                    contentDescription = null,
                    tint = (if (turnedOn) Color(0, 150, 0,) else Color.Unspecified)
                )
                Text(
                    text = run { if (turnedOn) "On" else "Off" },
                    modifier = Modifier.width(30.dp)
                )
            }
        )
    }
}