package com.quangkhai.getgo_application.data.network

import kotlinx.serialization.json.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    // Current weather at a coordinate. No API key required.
    // Whole response returned raw; the repository reads its "current" object.
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") long: Double,
        @Query("current") current: String =
            "temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code",
        @Query("timezone") timezone: String = "auto",
    ): JsonObject
}
