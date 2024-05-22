package org.vivlaniv.nexohub.entities.cards.pictures

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
fun THSensorCanvas() {
    Canvas(
        modifier = Modifier
            .size(100.dp)
            .background(color = Color.Transparent)
    ) {
        val u = size.minDimension / 10f
        val stroke = u / 2f
        drawPoints(
            points = listOf(
                Offset(u * 1f, u * 2f),
                Offset(u * 9f, u * 2f),
                Offset(u * 9f, u * 8f),
                Offset(u * 1f, u * 8f),
                Offset(u * 1f, u * 2f),
            ),
            pointMode = PointMode.Polygon,
            color = Color.Black,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawOval(
            color = Color.Blue,
            topLeft = Offset(u * 2.25f, u * 4f),
            size = Size(u * 2f, u * 3f)
        )
        drawLine(
            color = Color.Blue,
            start = Offset(u * 3.25f, u * 3f),
            end = Offset(u * 3.25f, u * 5f),
            strokeWidth = stroke
        )
        drawPoints(
            points = listOf(
                Offset(u * 2.3f, u * 5f),
                Offset(u * 3.25f, u * 3f),
                Offset(u * 4.2f, u * 5f),
            ),
            pointMode = PointMode.Polygon,
            color = Color.Black,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawOval(
            color = Color.Red,
            topLeft = Offset(u * 6f, u * 5f),
            size = Size(u * 2f, u * 2f),
        )
        drawRect(
            brush = Brush.verticalGradient(
                0.0f to Color.Red,
                0.2f to Color.Red,
                0.7f to Color(255, 100, 100),
                1.0f to Color.Transparent,
                startY = (u * 5.3f),
                endY = (u * 3f)
            ),
            topLeft = Offset(u * 6.25f, u * 3.2f),
            size = Size(u * 1.5f, u * 2.5f)
        )
        drawArc(
            color = Color.Black,
            startAngle = 310f,
            sweepAngle = 280f,
            useCenter = false,
            topLeft = Offset(u * 2.25f, u * 4f),
            size = Size(u * 2f, u * 3f),
            style = Stroke(width = stroke)
        )
        drawArc(
            color = Color.Black,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(u * 6.25f, u * 3f),
            size = Size(u * 1.5f, u * 2f),
            style = Stroke(width = stroke)
        )
        drawPoints(
            points = listOf(
                Offset(u * 6.25f, u * 5.3f),
                Offset(u * 6.25f, u * 3.8f),
                Offset(u * 7.75f, u * 5.3f),
                Offset(u * 7.75f, u * 3.8f),
            ),
            pointMode = PointMode.Lines,
            color = Color.Black,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawPoints(
            points = listOf(
                Offset(u * 7.50f, u * 5.2f),
                Offset(u * 7.25f, u * 5.2f),
                Offset(u * 7.50f, u * 4.7f),
                Offset(u * 7.25f, u * 4.7f),
                Offset(u * 7.50f, u * 4.2f),
                Offset(u * 7.25f, u * 4.2f),
            ),
            pointMode = PointMode.Lines,
            color = Color.Black,
            strokeWidth = stroke / 2,
            cap = StrokeCap.Round
        )
        drawArc(
            color = Color.Black,
            startAngle = 320f,
            sweepAngle = 260f,
            useCenter = false,
            topLeft = Offset(u * 6f, u * 5f),
            size = Size(u * 2f, u * 2f),
            style = Stroke(width = stroke)
        )
    }
}

@Preview
@Composable
fun THSensorCanvasPreview() {
    THSensorCanvas()
}