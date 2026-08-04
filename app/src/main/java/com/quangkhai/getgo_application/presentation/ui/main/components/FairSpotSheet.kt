package com.quangkhai.getgo_application.presentation.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

@Composable
fun FairSpotSheet(
    places: List<Location>,
    chosen: Location?,
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
            .padding(bottom = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
                .size(width = 44.dp, height = 5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(GetGoTheme.colors.fillElements)
        )

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

        if (chosen != null) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        chosen.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        chosen.address.ifBlank { "—" },
                        color = GetGoTheme.colors.labelColor,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                CircleTogglePill(circleVisible, onToggleCircle)
                Spacer(Modifier.width(8.dp))
                CloseMark(onClose)
            }

            Spacer(Modifier.height(6.dp))
            CoordinatesRow(chosen.lat, chosen.long)
        } else {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Fair spots (${places.size})",
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
            Spacer(Modifier.height(8.dp))
            Text(
                if (places.isEmpty()) "No spots found in this area."
                else "Tap a spot on the map to pick it.",
                color = GetGoTheme.colors.labelColor,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun CoordinatesRow(lat: Double, long: Double) {
    val clipboard = LocalClipboardManager.current
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("Coordinates", color = GetGoTheme.colors.labelColor, fontSize = 13.sp)
        Spacer(Modifier.weight(1f))
        Text(
            "$lat, $long",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.width(10.dp))
        Row(
            modifier = Modifier.clickable { clipboard.setText(AnnotatedString("$lat, $long")) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            CopyIcon(tint = GetGoTheme.colors.fillElements, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("copy", color = GetGoTheme.colors.fillElements, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun CircleTogglePill(circleVisible: Boolean, onToggleCircle: () -> Unit) {
    Text(
        "Circle visibility: ${if (circleVisible) "on" else "off"}",
        color = if (circleVisible) Color.White else GetGoTheme.colors.labelColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (circleVisible) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface)
            .border(
                1.5.dp,
                if (circleVisible) MaterialTheme.colorScheme.tertiary else GetGoTheme.colors.outlineElements,
                RoundedCornerShape(50)
            )
            .clickable { onToggleCircle() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
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
