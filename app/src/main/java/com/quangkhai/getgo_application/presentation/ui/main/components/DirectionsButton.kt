package com.quangkhai.getgo_application.presentation.ui.main.components
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

// bottom-right directions button — 42dp (10% larger than the left-stack buttons)
@Composable
fun DirectionsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickableOverMap { onClick() },
        contentAlignment = Alignment.Center
    ) {
        DirectionsIcon(tint = MaterialTheme.colorScheme.surface, modifier = Modifier.size(21.dp))
    }
}
