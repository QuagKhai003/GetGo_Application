package com.quangkhai.getgo_application.presentation.ui.main.components.magiccirlce

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quangkhai.getgo_application.presentation.ui.main.components.map.clickableOverMap
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

// Round on/off button for the magic circle. Off = pin icon; on = red ✕ to turn it off.
@Composable
fun MagicCircleButton(
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (active) GetGoTheme.colors.dangerous else MaterialTheme.colorScheme.secondary
    val iconTint = if (active) Color.White else Color.Black

    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(background)
            .clickableOverMap { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            if (active) Icons.Filled.Close else Icons.Filled.Place,
            contentDescription = if (active) "Turn off discovery" else "Discover places",
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
    }
}
