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
fun HumidifierCanvas(
    turn: Int = 0,
    humidity: Int = 0
) {
    val humidityScaled = remember(humidity) {
        (humidity / 100f).coerceIn(0f, 1f)
    }

    Canvas(
        modifier = Modifier
            .size(100.dp)
            .background(color = Color.Transparent)
    ) {
        val u = size.minDimension / 10f
        val stroke = u / 2f
        drawArc(
            color = Color.Gray,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(u * 3f, u * 3.5f),
            size = Size(u * 4f, u * 2f),
            style = Stroke(width = stroke)
        )
        drawOval(
            color = Color.Black,
            topLeft = Offset(u * 3f, u * 3f),
            size = Size(u * 4f, u * 2f),
            style = Stroke(width = stroke)
        )
        drawPoints(
            points = listOf(
                Offset(u * 4.5f, u * 4f),
                Offset(u * 5.5f, u * 4f)
            ),
            pointMode = PointMode.Polygon,
            color = Color.Black,
            strokeWidth = stroke / 2,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black,
            start = Offset(u * 3f, u * 4f),
            end = Offset(u * 3f, u * 9f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black,
            start = Offset(u * 7f, u * 4f),
            end = Offset(u * 7f, u * 9f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawArc(
            color = Color.Black,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(u * 3f, u * 4f),
            size = Size(u * 4f, u * 2f),
            style = Stroke(width = stroke)
        )
        drawArc(
            color = Color.Black,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(u * 3f, u * 8f - stroke / 2),
            size = Size(u * 4f, u * 2f),
            style = Stroke(width = stroke)
        )
        if (turn == 1) {
            drawArc(
                brush = Brush.radialGradient(
                    0.0f to Color.Transparent,
                    0.3f to Color(0, 0, 255, (humidityScaled * 255).toInt()),
                    0.8f to Color(0, 0, 255, (humidityScaled * 255).toInt()),
                    1.0f to Color.Transparent,
                    center = Offset(u * 5f, u * 4f),
                    radius = (u * 4f)
                ),
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = true,
                topLeft = Offset(u * 1f, 0f),
                size = Size(u * 8f, u * 8f)
            )
        }
    }
}

@Preview
@Composable
fun HumidifierCanvasPreview() {
    HumidifierCanvas(1, 60)
}