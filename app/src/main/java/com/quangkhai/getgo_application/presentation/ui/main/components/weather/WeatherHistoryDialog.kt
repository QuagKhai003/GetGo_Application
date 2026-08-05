package com.quangkhai.getgo_application.presentation.ui.main.components.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.quangkhai.getgo_application.domain.model.DayWeather
import com.quangkhai.getgo_application.domain.model.Weather
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

@Composable
fun WeatherHistoryDialog(
    weather: Weather?,
    history: List<DayWeather>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Weather here",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "✕",
                    color = GetGoTheme.colors.dangerous,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onDismiss() }
                )
            }

            if (weather != null) {
                Text(
                    "${weatherEmoji(weather.weatherCode)}  ${weather.temperatureC.toInt()}°C · ${weather.description}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            HorizontalDivider(thickness = 1.dp, color = GetGoTheme.colors.outlineElements, modifier = Modifier.padding(vertical = 4.dp))
            Text("Recent days", color = GetGoTheme.colors.labelColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)

            if (history.isEmpty()) {
                Text("No history available.", color = GetGoTheme.colors.labelColor, fontSize = 13.sp)
            } else {
                // most recent first (today at top)
                history.reversed().forEach { day ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(weatherEmoji(day.weatherCode), fontSize = 16.sp)
                        Text(prettyDate(day.date), color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text(
                            "${day.minC.toInt()}° – ${day.maxC.toInt()}°C",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// "2026-08-04" -> "04/08"
private fun prettyDate(iso: String): String {
    val parts = iso.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}" else iso
}
