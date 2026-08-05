package com.quangkhai.getgo_application.presentation.ui.main.components.magiccirlce

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.quangkhai.getgo_application.ui.theme.GetGoTheme
import kotlin.math.roundToInt

// Thin scrollbar on the right edge of a scroll area.
@Composable
fun BoxScope.ScrollbarThumb(scroll: ScrollState, trackHeightPx: Float) {
    if (scroll.maxValue <= 0) return

    val density = LocalDensity.current
    val minThumbPx = with(density) { 20.dp.toPx() }
    // thumb is shorter the more content overflows, but never smaller than minThumbPx
    val thumbHeightPx = (trackHeightPx * trackHeightPx / (trackHeightPx + scroll.maxValue))
        .coerceAtLeast(minThumbPx)

    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            // slide the thumb down in step with how far the list is scrolled
            .offset {
                val y = (scroll.value.toFloat() / scroll.maxValue) * (trackHeightPx - thumbHeightPx)
                IntOffset(0, y.roundToInt())
            }
            .width(3.dp)
            .height(with(density) { thumbHeightPx.toDp() })
            .clip(RoundedCornerShape(2.dp))
            .background(GetGoTheme.colors.outlineElements)
    )
}
