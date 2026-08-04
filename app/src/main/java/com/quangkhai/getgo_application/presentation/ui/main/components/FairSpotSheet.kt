package com.quangkhai.getgo_application.presentation.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

// Shown before a fair spot is picked: search status + a prompt to tap the map.
// Once a spot is chosen, MainScreen shows the normal PlaceDetailSheet instead.
@Composable
fun FairSpotSheet(
    places: List<Location>,
    onClose: () -> Unit,
    loading: Boolean = false,
    circleVisible: Boolean = true,
    onToggleCircle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, GetGoTheme.colors.outlineElements, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(horizontal = 15.dp)
            .padding(top = 16.dp, bottom = 14.dp)
    ) {
        if (loading && places.isEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Searching Fair Spot",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                CloseMark(onClose)
            }
            return@Column
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Tap a fair spot",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (places.isNotEmpty()) {
                CircleTogglePill(circleVisible, onToggleCircle)
                Spacer(Modifier.width(8.dp))
            }
            CloseMark(onClose)
        }
    }
}

@Composable
private fun CircleTogglePill(circleVisible: Boolean, onToggleCircle: () -> Unit) {
    Text(
        "Circle visibility: ${if (circleVisible) "on" else "off"}",
        color = if (circleVisible) MaterialTheme.colorScheme.tertiary else GetGoTheme.colors.labelColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onToggleCircle() }
    )
}

@Composable
private fun CloseMark(onClose: () -> Unit) {
    Text(
        "✕",
        color = GetGoTheme.colors.dangerous,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clickable { onClose() }
    )
}
