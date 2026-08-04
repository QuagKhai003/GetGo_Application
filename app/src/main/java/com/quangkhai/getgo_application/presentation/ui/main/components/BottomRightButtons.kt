package com.quangkhai.getgo_application.presentation.ui.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomRightButtons(
    onLocate: () -> Unit,
    onDirections: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GetCurLocationButton(onClick = onLocate)
        DirectionsButton(onClick = onDirections)
    }
}
