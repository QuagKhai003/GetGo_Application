package com.quangkhai.getgo_application.presentation.ui.main.components.magiccirlce

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

// The draggable, fixed-size circle the user positions on the map to search inside.
// Shows a "hold to drag" hint until they start dragging.
@Composable
fun MagicCircle(
    diameter: Dp,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDrag: (moveX: Float, moveY: Float) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
    draggable: Boolean = true
) {
    Box(
        modifier = modifier
            .size(diameter)
            .clip(CircleShape)
            .background(GetGoTheme.colors.fillElements)
            .border(2.dp, MaterialTheme.colorScheme.tertiary, CircleShape)
            .then(
                if (draggable) Modifier.holdToDragCircle(onDragStart, onDrag, onDragEnd)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (draggable && !isDragging) {
            Text(
                text = "hold to drag",
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
