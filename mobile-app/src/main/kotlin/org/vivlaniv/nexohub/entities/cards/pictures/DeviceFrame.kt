package org.vivlaniv.nexohub.entities.cards.pictures

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DeviceFrame(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(100.dp)
            .padding(8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
        color = Color.Transparent
    ) {
        Surface(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize(),
            color = Color.Transparent
        ) {
            content()
        }
    }
}