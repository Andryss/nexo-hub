package org.vivlaniv.nexohub.mobile.cards.property

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.vivlaniv.nexohub.common.PropertyInfo

@Composable
fun PercentDeviceProperty(
    propertyInfo: PropertyInfo,
    putPropertyCallback: (Int) -> Unit,
    shouldRefresh: Int
) {
    var percent by remember(shouldRefresh) {
        mutableFloatStateOf(propertyInfo.value.toFloat() / 100)
    }

    val showSlider = remember { !(propertyInfo.schema.isSensor ?: false) }

    fun convert(p: Float) = (p * 100).toInt()

    fun onSlideFinished() {
        putPropertyCallback(convert(percent))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.run { if (showSlider) fillMaxWidth() else fillMaxSize() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = propertyInfo.name)
                Text(text = "${convert(percent)}%")
            }
            if (showSlider) {
                Slider(
                    value = percent,
                    onValueChange = { percent = it },
                    modifier = Modifier.fillMaxWidth(),
                    onValueChangeFinished = { onSlideFinished() },
                    colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.inversePrimary),
                    enabled = !propertyInfo.readOnly
                )
            }
        }
    }
}