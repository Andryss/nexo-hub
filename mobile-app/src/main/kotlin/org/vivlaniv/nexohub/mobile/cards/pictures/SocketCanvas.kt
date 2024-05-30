package org.vivlaniv.nexohub.mobile.cards.pictures

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
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
fun SocketCanvas(
    turn: Int = 0
) {
    Canvas(
        modifier = Modifier
            .size(100.dp)
            .background(color = Color.Transparent)
    ) {
        val u = size.minDimension / 10f
        val stroke = u / 2f
        drawPoints(
            points = listOf(
                Offset(u * 1f, u * 4f),
                Offset(u * 5f, u * 6f),
                Offset(u * 9f, u * 4f),
                Offset(u * 5f, u * 2f),
                Offset(u * 1f, u * 4f)
            ),
            pointMode = PointMode.Polygon,
            color = Color.Black,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawPoints(
            points = listOf(
                Offset(u * 1f, u * 4f),
                Offset(u * 1f, u * 6f),
                Offset(u * 5f, u * 8f),
                Offset(u * 9f, u * 6f),
                Offset(u * 9f, u * 4f)
            ),
            pointMode = PointMode.Polygon,
            color = Color.Black,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawOval(
            color = Color.Black,
            topLeft = Offset(u * 3f, u * 3f),
            size = Size(u * 4f, u * 2f),
            style = Stroke(width = stroke)
        )
        drawPoints(
            points = listOf(
                Offset(u * 4f + stroke / 2, u * 4f),
                Offset(u * 6f - stroke / 2, u * 4f)
            ),
            pointMode = PointMode.Points,
            color = Color.Black,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawPoints(
            points = listOf(
                Offset(u * 3f, u * 7f),
                Offset(u * 3f, u * 9f),
                Offset(u * 7f, u * 9f),
                Offset(u * 7f, u * 7f)
            ),
            pointMode = PointMode.Polygon,
            color = Color.Black,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black,
            start = Offset(u * 4f, u * 9f),
            end = Offset(u * 4f, u * 10f - stroke / 2),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black,
            start = Offset(u * 6f, u * 9f),
            end = Offset(u * 6f, u * 10f - stroke / 2),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        if (turn == 1) {
            drawRect(
                color = Color.Gray,
                topLeft = Offset(u * 4f, 0f),
                size = Size(u * 2f, u * 2f)
            )
            drawLine(
                color = Color.Black,
                start = Offset(u * 4f, stroke / 2),
                end = Offset(u * 4f, u * 2f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.Black,
                start = Offset(u * 6f, stroke / 2),
                end = Offset(u * 6f, u * 2f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawArc(
                color = Color.Gray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(u * 3f, u * 3f),
                size = Size(u * 4f, u * 2f)
            )
            drawArc(
                color = Color.Black,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(u * 3f, u * 3f),
                size = Size(u * 4f, u * 2f),
                style = Stroke(width = stroke)
            )
            drawArc(
                color = Color.Gray,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(u * 3f, u * 1f),
                size = Size(u * 4f, u * 6f)
            )
            drawArc(
                color = Color.Black,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(u * 3f, u * 1f),
                size = Size(u * 4f, u * 6.1f),
                style = Stroke(width = stroke)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    0.5f to Color.Cyan,
                    1.0f to Color.Transparent,
                    center = Offset(u * 6f, u * 10f - stroke / 2),
                    radius = stroke
                ),
                radius = stroke,
                center = Offset(u * 6f, u * 10f - stroke)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    0.5f to Color.Cyan,
                    1.0f to Color.Transparent,
                    center = Offset(u * 4f, u * 10f - stroke / 2),
                    radius = stroke
                ),
                radius = stroke,
                center = Offset(u * 4f, u * 10f - stroke)
            )
        }
    }
}

@Preview
@Composable
fun SocketCanvasPreview() {
    SocketCanvas(1)
}