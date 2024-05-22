package org.vivlaniv.nexohub.entities.cards.pictures

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DefaultCanvas() {
    Canvas(
        modifier = Modifier
            .size(100.dp)
            .background(color = Color.Transparent)
    ) {
        val u = size.minDimension / 10f
        val stroke = u / 2f
        drawArc(
            color = Color.Black,
            startAngle = 180f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(u * 3f, u * 1f),
            size = Size(u * 4f, u * 4f),
            style = Stroke(width = stroke)
        )
        drawLine(
            color = Color.Black,
            start = Offset(u * 5f, u * 5f),
            end = Offset(u * 5f, u * 7f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = Color.Black,
            radius = stroke,
            center = Offset(u * 5f, u * 8f)
        )
    }
}

@Preview
@Composable
fun DefaultCanvasPreview() {
    DefaultCanvas()
}