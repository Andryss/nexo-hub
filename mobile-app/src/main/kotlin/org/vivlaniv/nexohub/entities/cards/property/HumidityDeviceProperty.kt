package org.vivlaniv.nexohub.entities.cards.property

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.vivlaniv.nexohub.PropertyInfo
import org.vivlaniv.nexohub.util.android.valueColor
import java.util.TreeMap

@Composable
fun HumidityDeviceProperty(
    propertyInfo: PropertyInfo,
    putPropertyCallback: (Int) -> Unit,
    shouldRefresh: Int
) {
    var humidity by remember(shouldRefresh) {
        mutableIntStateOf(propertyInfo.value)
    }

    val showSlider = remember { !(propertyInfo.schema.isSensor ?: false) }

    fun getPercent(): Float =
        ((humidity - 10).toFloat() / 70)

    fun convert(p: Float) =
        (10 + (p * 70)).toInt()

    fun onSlideFinished() {
        putPropertyCallback(humidity)
    }

    val colorMap = remember {
        TreeMap(mapOf(
            Float.MIN_VALUE to Color(255, 150, 0),
            0.00f to Color(255, 150, 0),
            0.30f to Color(0, 150, 255),
            1.00f to Color(0, 0, 255),
            Float.MAX_VALUE to Color(0, 0, 255)
        ))
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
                Row (
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${humidity}%")
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = valueColor(getPercent(), colorMap)
                    )
                }
            }
            if (showSlider) {
                Slider(
                    value = getPercent(),
                    onValueChange = { humidity = convert(it) },
                    modifier = Modifier.fillMaxWidth(),
                    onValueChangeFinished = { onSlideFinished() },
                    colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.inversePrimary),
                    enabled = !propertyInfo.readOnly
                )
            }
        }
    }
}