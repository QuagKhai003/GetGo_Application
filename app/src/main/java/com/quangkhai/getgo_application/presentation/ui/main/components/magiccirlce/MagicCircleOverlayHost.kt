package com.quangkhai.getgo_application.presentation.ui.main.components.magiccirlce

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.quangkhai.getgo_application.domain.usecase.map.CircleDiscoverUseCase
import kotlin.math.roundToInt

// Puts the circle on the map, and — when the user isn't dragging — the category
// tooltip just below it.
@Composable
fun MagicCircleOverlayHost(
    center: Offset,
    radiusPx: Float,
    circleDiameter: Dp,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDrag: (moveX: Float, moveY: Float) -> Unit,
    onDragEnd: () -> Unit,
    containerWidth: Int,
    selectedTerms: Set<String>,
    onToggleTerm: (String) -> Unit,
    onSearch: () -> Unit
) {
    MagicCircle(
        diameter = circleDiameter,
        isDragging = isDragging,
        onDragStart = onDragStart,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        modifier = Modifier.offset {
            IntOffset(
                (center.x - radiusPx).roundToInt(),
                (center.y - radiusPx).roundToInt()
            )
        }
    )

    if (!isDragging) {
        val density = LocalDensity.current
        val tooltipWidthPx = with(density) { 150.dp.toPx() }
        val gapPx = with(density) { 2.dp.toPx() }
        val tooltipX = (center.x - tooltipWidthPx / 2f)
            .coerceIn(0f, (containerWidth - tooltipWidthPx).coerceAtLeast(0f))
        val tooltipY = center.y + radiusPx + gapPx

        MagicCircleTooltip(
            categories = CircleDiscoverUseCase.CATEGORIES,
            selectedTerms = selectedTerms,
            onToggle = onToggleTerm,
            onSearch = onSearch,
            modifier = Modifier.offset {
                IntOffset(tooltipX.roundToInt(), tooltipY.roundToInt())
            }
        )
    }
}
