package com.quangkhai.getgo_application.presentation.ui.main.components.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomRightButtons(
    onLocate: () -> Unit,
    onDirections: () -> Unit,
    modifier: Modifier = Modifier,
    directionsActive: Boolean = false

) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GetCurLocationButton(onClick = onLocate)
        RoutingButton(onClick = onDirections, active = directionsActive)
    }
}
