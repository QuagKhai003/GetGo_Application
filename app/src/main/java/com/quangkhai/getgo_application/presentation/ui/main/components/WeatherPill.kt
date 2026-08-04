package com.quangkhai.getgo_application.presentation.ui.main.components

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.domain.model.Weather

@Composable
fun WeatherPill(weather: Weather?, modifier: Modifier = Modifier) {
    val label = if (weather == null) "🌡️ --°C"
        else "${weatherEmoji(weather.weatherCode)} ${weather.temperatureC.toInt()}°C"

    Text(
        text = label,
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        textAlign = TextAlign.Center
    )
}

// map a WMO weather code to a matching emoji
private fun weatherEmoji(code: Int): String {
    if (code == 0) return "☀️"
    if (code in 1..3) return "⛅"
    if (code == 45 || code == 48) return "🌫️"
    if (code in 51..67) return "🌧️"
    if (code in 71..77) return "❄️"
    if (code in 80..82) return "🌦️"
    if (code in 95..99) return "⛈️"
    return "☁️"
}
