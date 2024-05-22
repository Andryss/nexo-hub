package org.vivlaniv.nexohub.entities.cards.property

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.vivlaniv.nexohub.PropertyInfo
import org.vivlaniv.nexohub.widgets.Selector

@Composable
fun EnumDeviceProperty(
    propertyInfo: PropertyInfo,
    putPropertyCallback: (Int) -> Unit,
    shouldRefresh: Int
) {
    val values = remember { propertyInfo.schema.enumValues!! }

    var selected by remember(shouldRefresh) {
        mutableIntStateOf(propertyInfo.value)
    }

    fun onSelected(newValue: String) {
        selected = values.indexOf(newValue)
        putPropertyCallback(selected)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = propertyInfo.name)

        Selector(
            options = propertyInfo.schema.enumValues!!,
            selected = values[selected],
            onItemSelected = { onSelected(it) }
        )
    }
}
