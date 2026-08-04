package com.quangkhai.getgo_application.domain.model

// one day of weather (used for the recent history list)
data class DayWeather(
    val date: String,      // YYYY-MM-DD
    val maxC: Double,
    val minC: Double,
    val weatherCode: Int
)
