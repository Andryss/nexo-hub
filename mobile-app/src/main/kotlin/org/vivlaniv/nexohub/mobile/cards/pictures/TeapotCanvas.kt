package org.vivlaniv.nexohub.mobile.cards.pictures

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TeapotCanvas(
    volume: Int = 0,
    temperature: Int = 0
) {
    val volumeScaled = remember(volume) { volume / 100f }
    val temperatureScaled = remember(temperature) { temperature / 100f * 0.6f }

    Canvas(
        modifier = Modifier
            .size(100.dp)
            .background(color = Color.Transparent)
    ) {
        val u = size.minDimension / 10f
        val stroke = u / 2f
        drawRect(
            color = Color.Cyan,
            topLeft = Offset(u * 3f, u * 3f - stroke + u * 7f * (1 - volumeScaled)),
            size = Size(u * 4f, u * 7f * volumeScaled)
        )
        drawRect(
            brush = Brush.verticalGradient(
                (1.0f - temperatureScaled * volumeScaled) to Color.Transparent,
                1.0f to Color.Red
            ),
            topLeft = Offset(u * 3f, u * 3f - stroke + u * 7f * (1 - volumeScaled)),
            size = Size(u * 4f, u * 7f * volumeScaled)
        )
        drawPoints(
            points = listOf(
                Offset(u * 7f, u * 2f),
                Offset(u * 7f, u * 10f - stroke / 2),
                Offset(u * 3f, u * 10f - stroke / 2),
                Offset(u * 3f, u * 3f),
                Offset(u * 2f, u * 2f),
                Offset(u * 7f, u * 2f),
            ),
            pointMode = PointMode.Polygon,
            color = Color.Black,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawPoints(
            points = listOf(
                Offset(u * 4f, u * 2f),
                Offset(u * 4f, u * 1f),
                Offset(u * 6f, u * 1f),
                Offset(u * 6f, u * 2f),
            ),
            pointMode = PointMode.Polygon,
            color = Color.Black,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawArc(
            color = Color.Black,
            startAngle = 270f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(u * 5f, u * 3f),
            size = Size(u * 4f, u * 4f),
            style = Stroke(width = stroke)
        )
    }
}

@Preview
@Composable
fun TeapotCanvasPreview() {
    TeapotCanvas(100, 80)
}