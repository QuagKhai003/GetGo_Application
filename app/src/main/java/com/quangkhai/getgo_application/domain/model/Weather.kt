package com.quangkhai.getgo_application.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Weather(

    val temperatureC: Double,

    val humidity: Int,

    val windSpeedKmh: Double,

    val weatherCode: Int,

    // human-readable label mapped from weatherCode (WMO code)
    val description: String,
)
