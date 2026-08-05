package com.quangkhai.getgo_application.presentation.ui.main.components.magiccirlce

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.domain.usecase.map.DiscoverCategory
import com.quangkhai.getgo_application.presentation.ui.main.components.map.clickableOverMap
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

// The category card that drops down under the circle - a scrolling list
// of place-type checkboxes, and the Discover button that runs the search.
@Composable
fun MagicCircleTooltip(
    categories: List<DiscoverCategory>,
    selectedTerms: Set<String>,
    onToggle: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val surface = MaterialTheme.colorScheme.surface
    val outline = GetGoTheme.colors.outlineElements
    val strokePx = with(LocalDensity.current) { 1.5.dp.toPx() }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // small upward caret so the card looks like it points at the circle above
        Canvas(modifier = Modifier.size(width = 16.dp, height = 8.dp)) {
            val fillPath = Path().apply {
                moveTo(size.width / 2f, 0f)
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                close()
            }
            drawPath(fillPath, surface)
            // outline just the two slanted sides, leaving the bottom open where it joins the card
            val edgePath = Path().apply {
                moveTo(0f, size.height)
                lineTo(size.width / 2f, 0f)
                lineTo(size.width, size.height)
            }
            drawPath(edgePath, outline, style = Stroke(width = strokePx))
        }

        Column(
            modifier = Modifier
                .width(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(surface)
                .border(1.5.dp, outline, RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Text(
                text = "Preferred location",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 2.dp, bottom = 5.dp)
            )

            // scrolling list about 3 rows tall, with its own scrollbar
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
            ) {
                val scroll = rememberScrollState()
                val trackPx = with(LocalDensity.current) { maxHeight.toPx() }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scroll)
                        .padding(end = 6.dp)   // leave room for the scrollbar
                ) {
                    categories.forEach { category ->
                        CheckRow(
                            label = category.label,
                            checked = category.term in selectedTerms,
                            onClick = { onToggle(category.term) }
                        )
                    }
                }

                ScrollbarThumb(scroll = scroll, trackHeightPx = trackPx)
            }

            // Discover button that runs the search
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Discover",
                    color = MaterialTheme.colorScheme.surface,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .clickableOverMap { onSearch() }
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// one compact checkbox row (Material's Checkbox is too big for this small card)
@Composable
private fun CheckRow(
    label: String,
    checked: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickableOverMap { onClick() }
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val boxColor =
            if (checked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(boxColor)
                .border(1.5.dp, GetGoTheme.colors.outlineElements, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp,
            modifier = Modifier.padding(start = 7.dp)
        )
    }
}
