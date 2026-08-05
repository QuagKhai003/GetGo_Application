package com.quangkhai.getgo_application.presentation.ui.main.components.magiccirlce

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlin.math.hypot

// Drag from anywhere inside the circle to move it, without the map panning underneath
fun Modifier.holdToDragCircle(
    onDragStart: () -> Unit,
    onDrag: (moveX: Float, moveY: Float) -> Unit,
    onDragEnd: () -> Unit
): Modifier = this.pointerInput(Unit) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
        var dragging = false
        var moved = 0f
        val slop = viewConfiguration.touchSlop
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val change = event.changes.firstOrNull { it.id == down.id } ?: break
            if (!change.pressed) break
            val delta = change.positionChange()
            if (!dragging) {
                moved += hypot(delta.x, delta.y)
                if (moved > slop) {
                    dragging = true
                    onDragStart()
                }
            }
            if (dragging && delta != Offset.Zero) {
                onDrag(delta.x, delta.y)
                change.consume()
            }
        }
        if (dragging) onDragEnd()
    }
}
