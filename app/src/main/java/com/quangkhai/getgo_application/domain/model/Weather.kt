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

    // timestamp of the reading from the API, e.g. "2026-08-05T15:00"
    val time: String = "",

    // place name from the API timezone, e.g. "Ho Chi Minh"
    val place: String = "",
)
