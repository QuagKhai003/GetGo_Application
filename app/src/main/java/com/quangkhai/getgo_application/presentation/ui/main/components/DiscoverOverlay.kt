package com.quangkhai.getgo_application.presentation.ui.main.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.domain.usecase.map.CircleDiscoverUseCase
import com.quangkhai.getgo_application.domain.usecase.map.DiscoverCategory
import com.quangkhai.getgo_application.ui.theme.GetGoTheme
import kotlin.math.roundToInt

/**
 * Round toggle for discovery mode. Inactive: neutral pin. Active: red ✕ (turn off).
 */
@Composable
fun DiscoverButton(
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

/**
 * The draggable, fixed-size discovery circle. Hold anywhere inside and move to
 * reposition; the map stays zoomable. Shows a "hold to drag" hint (hidden while dragging).
 */
@Composable
fun DiscoverCircle(
    diameter: androidx.compose.ui.unit.Dp,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDrag: (dx: Float, dy: Float) -> Unit,
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
            // Hold anywhere inside and move to reposition. Drag on the Initial pass so the
            // osmdroid map (which claims touches on Initial) does not pan instead. A plain
            // tap is never consumed, so it falls through to the map dots underneath.
            .then(if (draggable) Modifier.pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    var dragging = false
                    var moved = 0f
                    val slop = viewConfiguration.touchSlop
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) break
                        val delta = change.positionChange()
                        if (!dragging) {
                            moved += kotlin.math.hypot(delta.x, delta.y)
                            if (moved > slop) {
                                dragging = true
                                onDragStart()
                            }
                        }
                        if (dragging && delta != Offset.Zero) {
                            onDrag(delta.x, delta.y)
                            change.consume()
                        }
                    }
                    if (dragging) onDragEnd()
                }
            } else Modifier),
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

/**
 * Small tooltip shown under the circle: an upward caret (like the pin bubble),
 * a scrollable checkbox list with a visible scrollbar, and a fixed pill button.
 */
@Composable
fun DiscoverTooltip(
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
        // upward caret pointing at the circle above (filled + outlined so it shows on the map)
        Canvas(modifier = Modifier.size(width = 16.dp, height = 8.dp)) {
            val fillPath = Path().apply {
                moveTo(size.width / 2f, 0f)
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                close()
            }
            drawPath(fillPath, surface)
            // outline only the two slanted edges (not the base that meets the card)
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

            // fixed-height scroll area (shows ~3 rows) with a visible scrollbar thumb
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
            ) {
                val scroll = rememberScrollState()
                val density = LocalDensity.current
                val trackPx = with(density) { maxHeight.toPx() }

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

                // scrollbar thumb (only when content overflows)
                if (scroll.maxValue > 0) {
                    val thumbPx = (trackPx * trackPx / (trackPx + scroll.maxValue))
                        .coerceAtLeast(with(density) { 20.dp.toPx() })

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            // read scroll.value here (deferred) so scrolling doesn't recompose
                            .offset {
                                val y = (scroll.value.toFloat() / scroll.maxValue) * (trackPx - thumbPx)
                                IntOffset(0, y.roundToInt())
                            }
                            .width(3.dp)
                            .height(with(density) { thumbPx.toDp() })
                            .clip(RoundedCornerShape(2.dp))
                            .background(GetGoTheme.colors.outlineElements)
                    )
                }
            }

            // fixed pill button
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

// Draggable circle + (when not dragging) the category tooltip, positioned by px.
@Composable
fun DiscoverOverlayHost(
    center: Offset,
    radiusPx: Float,
    circleDiameter: androidx.compose.ui.unit.Dp,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: () -> Unit,
    containerWidth: Int,
    selectedTerms: Set<String>,
    onToggleTerm: (String) -> Unit,
    onSearch: () -> Unit
) {
    DiscoverCircle(
        diameter = circleDiameter,
        isDragging = isDragging,
        onDragStart = onDragStart,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        modifier = Modifier.offset {
            IntOffset(
                (center.x - radiusPx).roundToInt(),
                (center.y - radiusPx).roundToInt()
            )
        }
    )

    if (!isDragging) {
        val density = LocalDensity.current
        val tooltipWidthPx = with(density) { 150.dp.toPx() }
        val gapPx = with(density) { 2.dp.toPx() }
        val tooltipX = (center.x - tooltipWidthPx / 2f)
            .coerceIn(0f, (containerWidth - tooltipWidthPx).coerceAtLeast(0f))
        val tooltipY = center.y + radiusPx + gapPx

        DiscoverTooltip(
            categories = CircleDiscoverUseCase.CATEGORIES,
            selectedTerms = selectedTerms,
            onToggle = onToggleTerm,
            onSearch = onSearch,
            modifier = Modifier.offset {
                IntOffset(tooltipX.roundToInt(), tooltipY.roundToInt())
            }
        )
    }
}

// Compact custom checkbox row (Material Checkbox forces a large touch size).
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
