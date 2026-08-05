package com.quangkhai.getgo_application.presentation.ui.main.components.magiccirlce

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp

// The magic-circle discovery overlay: the draggable circle + its category tooltip.
// Shown only while discover mode is on.
@Composable
fun MagicCircleLayer(
    discoverMode: Boolean,
    circleCenter: Offset?,
    radiusPx: Float,
    circleDiameter: Dp,
    isDragging: Boolean,
    containerWidth: Int,
    selectedTerms: Set<String>,
    onDragStart: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onToggleTerm: (String) -> Unit,
    onSearch: () -> Unit
) {
    if (!discoverMode || circleCenter == null) return

    MagicCircleOverlayHost(
        center = circleCenter,
        radiusPx = radiusPx,
        circleDiameter = circleDiameter,
        isDragging = isDragging,
        onDragStart = onDragStart,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        containerWidth = containerWidth,
        selectedTerms = selectedTerms,
        onToggleTerm = onToggleTerm,
        onSearch = onSearch
    )
}
