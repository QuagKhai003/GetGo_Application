package com.quangkhai.getgo_application.presentation.ui.main.components.fairspot
import com.quangkhai.getgo_application.presentation.ui.main.components.magiccirlce.MagicCircle

import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

// The draggable search circle + its "Search area" button, positioned over the map by pixels.
// Shown only in fair-spot mode while the circle is visible and not mid-search.
@Composable
fun FairSpotMapLayer(
    show: Boolean,
    circlePos: Offset?,
    circleDiameter: Dp,
    radiusPx: Float,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onSearchArea: () -> Unit
) {
    if (!show || circlePos == null) return

    MagicCircle(
        diameter = circleDiameter,
        isDragging = isDragging,
        onDragStart = onDragStart,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        modifier = Modifier.offset {
            IntOffset(
                (circlePos.x - radiusPx).roundToInt(),
                (circlePos.y - radiusPx).roundToInt()
            )
        }
    )

    SearchThisAreaButton(
        onClick = onSearchArea,
        modifier = Modifier.offset {
            IntOffset(
                (circlePos.x - 44.dp.toPx()).roundToInt(),
                (circlePos.y + radiusPx + 10.dp.toPx()).roundToInt()
            )
        }
    )
}
