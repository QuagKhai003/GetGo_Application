package com.quangkhai.getgo_application.presentation.ui.main.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

/**
 * A tap handler for buttons drawn ON TOP of the osmdroid map.
 *
 * The map is an embedded Android View (via AndroidView). It claims touch events
 * on the *Initial* pointer pass, so a normal Modifier.clickable — which listens
 * on the Main pass — never fires: the tap falls through to the map (dropping a
 * pin) instead of pressing the button.
 *
 * Here we grab the DOWN and the UP on the Initial pass and consume them, so the
 * tap lands on the button and the map never sees it. onClick fires only when the
 * finger lifts inside the button's bounds.
 */
fun Modifier.clickableOverMap(onClick: () -> Unit): Modifier = this.pointerInput(onClick) {
    awaitEachGesture {
        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial).consume()
        val up = waitForUpOrCancellation(PointerEventPass.Initial)
        if (up != null) {
            up.consume()
            val insideButton = up.position.x in 0f..size.width.toFloat() &&
                up.position.y in 0f..size.height.toFloat()
            if (insideButton) onClick()
        }
    }
}
