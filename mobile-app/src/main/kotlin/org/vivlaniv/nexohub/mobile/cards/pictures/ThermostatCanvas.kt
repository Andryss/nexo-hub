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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ThermostatCanvas(
    turn: Int = 0,
    temperature: Int = 0
) {
    val temperatureScaled = remember(temperature) {
        (temperature.coerceIn(10, 40) - 10) / 30f
    }

    val brush = remember(temperature) {
        Brush.verticalGradient(
            0.0f to Color.Transparent,
            0.5f to run {
                val lr = 0f; val lg = 0f; val lb = 0.6f
                val hr = 0.6f; val hg = 0f; val hb = 0f
                Color(
                    lr + (hr - lr) * temperatureScaled,
                    lg + (hg - lg) * temperatureScaled,
                    lb + (hb - lb) * temperatureScaled
                )
            },
            1.0f to Color.Transparent,
        )
    }

    Canvas(
        modifier = Modifier
            .size(100.dp)
            .background(color = Color.Transparent)
    ) {
        val u = size.minDimension / 10f
        val stroke = u / 2f
        if (turn == 1) {
            drawRect(
                brush = brush,
                topLeft = Offset(u * 1f, u * 1f),
                size = Size(u * 2f, u * 8f)
            )
            drawRect(
                brush = brush,
                topLeft = Offset(u * 4f, u * 1f),
                size = Size(u * 2f, u * 8f)
            )
            drawRect(
                brush = brush,
                topLeft = Offset(u * 7f, u * 1f),
                size = Size(u * 2f, u * 8f)
            )
        }
        drawPoints(
            points = listOf(
                Offset(u * 1f, u * 1f),
                Offset(u * 3f, u * 1f),
                Offset(u * 3f, u * 9f),
                Offset(u * 1f, u * 9f),
                Offset(u * 1f, u * 1f),
            ),
            pointMode = PointMode.Polygon,
            color = Color.Black,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawPoints(
            points = listOf(
                Offset(u * 4f, u * 1f),
                Offset(u * 6f, u * 1f),
                Offset(u * 6f, u * 9f),
                Offset(u * 4f, u * 9f),
                Offset(u * 4f, u * 1f),
            ),
            pointMode = PointMode.Polygon,
            color = Color.Black,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawPoints(
            points = listOf(
                Offset(u * 7f, u * 1f),
                Offset(u * 9f, u * 1f),
                Offset(u * 9f, u * 9f),
                Offset(u * 7f, u * 9f),
                Offset(u * 7f, u * 1f),
            ),
            pointMode = PointMode.Polygon,
            color = Color.Black,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black,
            start = Offset(u * 3f, u * 2f),
            end = Offset(u * 4f, u * 2f),
            strokeWidth = stroke
        )
        drawLine(
            color = Color.Black,
            start = Offset(u * 3f, u * 8f),
            end = Offset(u * 4f, u * 8f),
            strokeWidth = stroke
        )
        drawLine(
            color = Color.Black,
            start = Offset(u * 6f, u * 2f),
            end = Offset(u * 7f, u * 2f),
            strokeWidth = stroke
        )
        drawLine(
            color = Color.Black,
            start = Offset(u * 6f, u * 8f),
            end = Offset(u * 7f, u * 8f),
            strokeWidth = stroke
        )
    }
}

@Preview
@Composable
fun ThermostatCanvasPreview() {
    ThermostatCanvas(1, 30)
}