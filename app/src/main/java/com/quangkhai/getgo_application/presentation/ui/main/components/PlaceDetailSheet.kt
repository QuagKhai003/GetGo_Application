package com.quangkhai.getgo_application.presentation.ui.main.components
import com.quangkhai.getgo_application.ui.theme.GetGoTheme
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.domain.model.Location

/**
 * The place sheet, anchored to the bottom of the screen. Its height is animated
 * by the caller; content taller than the current height is clipped, which gives
 * the peek. Drag up/down to expand or collapse; tapping also toggles.
 */
@Composable
fun PlaceDetailSheet(
    location: Location,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    isFavorite: Boolean,
    onSave: () -> Unit,
    onRemove: () -> Unit,
    onAddBill: () -> Unit,
    onClose: () -> Unit,
    circleVisible: Boolean? = null,
    onToggleCircle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.5.dp, GetGoTheme.colors.outlineElements,
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .pointerInput(Unit) {
                var total = 0f
                detectVerticalDragGestures(
                    onDragStart = { total = 0f },
                    onDragEnd = {
                        if (total < -30f) onExpandedChange(true)      // dragged up
                        else if (total > 30f) onExpandedChange(false) // dragged down
                    },
                    onVerticalDrag = { _, dy -> total += dy }
                )
            }
            .clickable { onExpandedChange(!expanded) }
            .padding(horizontal = 15.dp)
    ) {
        // drag handle
        Box(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
                .size(width = 40.dp, height = 4.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(GetGoTheme.colors.outlineElements)
        )

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(location.name, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(location.address.ifBlank { "—" }, color = GetGoTheme.colors.labelColor, fontSize = 12.sp)
            }
            if (circleVisible != null) {
                CircleTogglePill(circleVisible, onToggleCircle)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                "✕",
                color = GetGoTheme.colors.dangerous,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onClose() }
                    .padding(start = 8.dp, top = 2.dp)
            )
        }

        Column(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            CoordinatesRow(location.lat, location.long)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isFavorite) {
                    SheetButton("Remove Favorite", primary = false, onClick = onRemove, modifier = Modifier.weight(1f))
                } else {
                    SheetButton("Save Location", primary = true, onClick = onSave, modifier = Modifier.weight(1f))
                }
                SheetButton("Add bill", primary = false, onClick = onAddBill, modifier = Modifier.weight(1f))
            }
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
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onToggleCircle() }
            .padding(top = 2.dp)
    )
}

@Composable
private fun CoordinatesRow(lat: Double, long: Double) {
    val clipboard = LocalClipboardManager.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Coordinates", color = GetGoTheme.colors.labelColor, fontSize = 13.sp)
            Spacer(Modifier.width(8.dp))
            Row(
                modifier = Modifier.clickable { clipboard.setText(AnnotatedString("$lat, $long")) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                CopyIcon(tint = GetGoTheme.colors.fillElements, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(3.dp))
                Text("copy", color = GetGoTheme.colors.fillElements, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
        Text(
            "$lat, $long",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SheetButton(
    label: String,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        color = if (primary) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.tertiary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (primary) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface)
            .border(1.5.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}
