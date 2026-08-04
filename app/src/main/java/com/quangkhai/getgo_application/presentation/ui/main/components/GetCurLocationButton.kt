package com.quangkhai.getgo_application.presentation.ui.main.components
import com.quangkhai.getgo_application.ui.theme.GetGoTheme
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

// white button (same 42dp as Directions) that centres the map on the user
@Composable
fun GetCurLocationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, GetGoTheme.colors.outlineElements, CircleShape)
            .clickableOverMap { onClick() },   // Initial-pass tap so the map underneath doesn't steal it
        contentAlignment = Alignment.Center
    ) {
        MyLocationIcon(tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(22.dp))
    }
}
