package com.quangkhai.getgo_application.presentation.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.domain.model.Weather
import com.quangkhai.getgo_application.presentation.ui.main.components.weather.weatherEmoji
import com.quangkhai.getgo_application.ui.theme.GetGo_ApplicationTheme


@Composable
fun MenuCard(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    icon: ImageVector? = null
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .dropShadow(
                shape = RoundedCornerShape(16.dp),
                shadow = Shadow(
                    color = Color.Black.copy(0.25F),
                    offset = DpOffset(x = 0.dp, y = 8.dp),
                    radius = 8.dp,
                    spread = 1.dp
                )
            )
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(16.dp),

        contentAlignment = Alignment.BottomStart

    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(58.dp)
            )
        }
        Text(
            text = label,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Weather variant of the menu card: same shape/shadow, but shows live weather
 * instead of a plain label. Tapping reloads (no navigation).
 */
@Composable
fun WeatherMenuCard(
    weather: Weather?,
    onClick: () -> Unit,
    modifier: Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .dropShadow(
                shape = RoundedCornerShape(16.dp),
                shadow = Shadow(
                    color = Color.Black.copy(0.25F),
                    offset = DpOffset(x = 0.dp, y = 8.dp),
                    radius = 8.dp,
                    spread = 1.dp
                )
            )
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = if (weather != null) weatherEmoji(weather.weatherCode) else "🌡️",
            fontSize = 42.sp,
            modifier = Modifier.align(Alignment.TopEnd)
        )
        Column {
            if (weather != null) {
                Text(
                    text = "${weather.temperatureC.toInt()}°C",
                    color = textColor,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = weather.description,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "Today's\nWeather",
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (weather == null) {
                Text(
                    text = "Tap to load",
                    color = textColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuCardPreview() {
    GetGo_ApplicationTheme {
        MenuCard(
            label = "Let's\nGet Go",
            onClick = {},
            modifier = Modifier
        )
    }
}




