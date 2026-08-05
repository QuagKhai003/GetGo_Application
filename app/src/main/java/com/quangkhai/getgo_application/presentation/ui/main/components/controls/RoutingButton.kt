package com.quangkhai.getgo_application.presentation.ui.main.components.controls
import com.quangkhai.getgo_application.presentation.ui.main.components.map.DirectionsIcon
import com.quangkhai.getgo_application.presentation.ui.main.components.map.clickableOverMap
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

// bottom-right directions button. When a route is drawn it turns into a red ✕ that removes it.
@Composable
fun RoutingButton(
    onClick: () -> Unit,
    active: Boolean = false,
    modifier: Modifier = Modifier
) {
    val background = if (active) GetGoTheme.colors.dangerous else MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(background)
            .clickableOverMap { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (active) {
            Icon(Icons.Filled.Close, contentDescription = "Remove route", tint = Color.White, modifier = Modifier.size(20.dp))
        } else {
            DirectionsIcon(tint = MaterialTheme.colorScheme.surface, modifier = Modifier.size(21.dp))
        }
    }
}
