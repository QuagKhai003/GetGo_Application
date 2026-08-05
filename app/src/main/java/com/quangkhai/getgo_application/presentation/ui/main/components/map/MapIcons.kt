package com.quangkhai.getgo_application.presentation.ui.main.components.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

// four-square menu glyph (2x2 dots) — drawn so it doesn't need material-icons-extended
@Composable
fun FourSquareIcon(tint: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(2) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(2) {
                    Box(Modifier.size(5.dp).clip(RoundedCornerShape(1.5.dp)).background(tint))
                }
            }
        }
    }
}

// directions diamond + arrow, drawn on a canvas
@Composable
fun DirectionsIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val sw = size.minDimension * 0.09f
        val diamond = Path().apply {
            moveTo(w / 2, sw); lineTo(w - sw, h / 2); lineTo(w / 2, h - sw); lineTo(sw, h / 2); close()
        }
        drawPath(diamond, color = tint, style = Stroke(width = sw))
        val arrow = Path().apply {
            moveTo(w * 0.36f, h * 0.5f); lineTo(w * 0.58f, h * 0.5f); lineTo(w * 0.58f, h * 0.64f)
        }
        drawPath(arrow, color = tint, style = Stroke(width = sw, cap = StrokeCap.Round))
    }
}

// copy glyph: two overlapping rounded sheets
@Composable
fun CopyIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val sw = size.minDimension * 0.09f
        val radius = CornerRadius(size.minDimension * 0.16f, size.minDimension * 0.16f)
        // back sheet (upper-right)
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.34f, h * 0.06f),
            size = Size(w * 0.58f, h * 0.58f),
            cornerRadius = radius,
            style = Stroke(width = sw)
        )
        // front sheet (lower-left)
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.08f, h * 0.36f),
            size = Size(w * 0.58f, h * 0.58f),
            cornerRadius = radius,
            style = Stroke(width = sw)
        )
    }
}

// "my location" crosshair: outer ring, centre dot, four ticks
@Composable
fun MyLocationIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val cx = size.width / 2
        val cy = size.height / 2
        val d = size.minDimension
        val sw = d * 0.08f
        drawCircle(tint, radius = d * 0.28f, center = Offset(cx, cy), style = Stroke(width = sw))
        drawCircle(tint, radius = d * 0.10f, center = Offset(cx, cy))
        val inner = d * 0.28f + sw
        val outer = d * 0.46f
        drawLine(tint, Offset(cx, cy - inner), Offset(cx, cy - outer), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(tint, Offset(cx, cy + inner), Offset(cx, cy + outer), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(tint, Offset(cx - inner, cy), Offset(cx - outer, cy), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(tint, Offset(cx + inner, cy), Offset(cx + outer, cy), strokeWidth = sw, cap = StrokeCap.Round)
    }
}
