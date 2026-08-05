package com.quangkhai.getgo_application.presentation.ui.main.components.controls
import com.quangkhai.getgo_application.presentation.ui.main.components.map.FourSquareIcon
import com.quangkhai.getgo_application.presentation.ui.main.components.map.clickableOverMap

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

@Composable
fun BottomLeftButtons(
    menuOpen: Boolean,
    onToggleMenu: () -> Unit,
    onHome: () -> Unit,
    favoritesActive: Boolean,
    onFavoritesChange: (Boolean) -> Unit,
    myLocationActive: Boolean,
    onMyLocationChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonSize = 42.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AnimatedVisibility(
            visible = menuOpen,
            enter = fadeIn() + scaleIn(initialScale = 0.7f),
            exit = fadeOut() + scaleOut(targetScale = 0.7f)
        ) {
            Column(
                modifier = Modifier
                    .width(165.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.5.dp, GetGoTheme.colors.outlineElements, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                PreferenceButton("Favorites", favoritesActive, onFavoritesChange)
                PreferenceButton("My Location", myLocationActive, onMyLocationChange)
            }
        }

        RoundButton(size = buttonSize, background = MaterialTheme.colorScheme.secondary, onClick = onToggleMenu) {
            if (menuOpen) {
                Icon(Icons.Filled.Close, "Close menu", tint = Color.Black, modifier = Modifier.size(20.dp))
            } else {
                FourSquareIcon(tint = Color.Black)
            }
        }

        RoundButton(size = buttonSize, background = MaterialTheme.colorScheme.secondary, onClick = onHome) {
            Icon(Icons.Filled.Home, "Home", tint = Color.Black, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun RoundButton(
    size: Dp,
    background: Color,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .clickableOverMap { onClick() },
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun PreferenceButton(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(0.7f)
        )
    }
}
