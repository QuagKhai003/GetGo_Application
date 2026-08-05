package com.quangkhai.getgo_application.presentation.ui.main.components.magiccirlce

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.presentation.ui.main.components.map.clickableOverMap

// The little "Magic Discover Circle" chip that turns the mode on.
@Composable
fun MagicCirclePill(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = "Magic Discover Circle",
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.tertiary)
            .clickableOverMap { onClick() }
            .padding(horizontal = 16.dp, vertical = 4.dp)
    )
}
