package com.quangkhai.getgo_application.presentation.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

// Rounded box shown above the fair-spot sheet after a spot is picked. Lists each
// member's travel distance, up to 4 per row (FlowRow wraps automatically).
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FairMemberBox(
    distances: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (distances.isEmpty()) return

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, GetGoTheme.colors.outlineElements, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        maxItemsInEachRow = 4
    ) {
        distances.forEach { (name, meters) ->
            val distance = if (meters < 1000) "${meters.toInt()} m"
            else String.format(java.util.Locale.US, "%.1f km", meters / 1000)
            Text(
                "$name : $distance",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
