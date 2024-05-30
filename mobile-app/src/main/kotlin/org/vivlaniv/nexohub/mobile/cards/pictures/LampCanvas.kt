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
import org.vivlaniv.nexohub.common.util.RGBToColor
import org.vivlaniv.nexohub.common.util.colorToRGB

@Composable
fun LampCanvas(
    turn: Int = 0,
    brightness: Int = 0,
    color: Int = 0
) {
    val (red, green, blue) = remember(color) { colorToRGB(color) }
    val brightnessScaled = remember(brightness) { (brightness / 100f * 255f).toInt() }
    
    Canvas(
        modifier = Modifier
            .size(100.dp)
            .background(color = Color.Transparent)
    ) {
        val u = size.minDimension / 10f
        val stroke = u / 2f
        if (turn == 1) {
            drawCircle(
                brush = Brush.radialGradient(
                    0.0f to Color(red, green, blue, brightnessScaled),
                    0.2f to Color(red, green, blue, brightnessScaled),
                    0.5f to Color(red, green, blue, brightnessScaled),
                    0.7f to Color(red, green, blue, brightnessScaled / 2),
                    1.0f to Color.Transparent,
                    center = Offset(u * 5f, u * 5f),
                ),
                radius = size.maxDimension
            )
        }
        drawArc(
            color = Color.Black,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(u * 2f, u * 2f),
            size = Size(u * 6f, u * 6f),
            style = Stroke(width = stroke)
        )
        drawPoints(
            points = listOf(
                Offset(u * 3.5f, u * 7f + stroke),
                Offset(u * 3.5f, u * 10f - stroke / 2),
                Offset(u * 6.5f, u * 10f - stroke / 2),
                Offset(u * 6.5f, u * 7f + stroke)
            ),
            pointMode = PointMode.Polygon,
            color = Color.Black,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black,
            start = Offset(u * 3f, u * 10f - stroke * 2),
            end = Offset(u * 7f, u * 10f - stroke * 2),
            strokeWidth = stroke * 0.8f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black,
            start = Offset(u * 3f, u * 10f - stroke * 3),
            end = Offset(u * 7f, u * 10f - stroke * 3),
            strokeWidth = stroke * 0.8f,
            cap = StrokeCap.Round
        )
    }
}

@Preview
@Composable
fun LampCanvasPreview() {
    LampCanvas(1, 80, RGBToColor(50, 200, 30))
}