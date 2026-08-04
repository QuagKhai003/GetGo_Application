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

    // recent daily history: the last `past_days` days + today
    @GET("v1/forecast")
    suspend fun getWeatherHistory(
        @Query("latitude") lat: Double,
        @Query("longitude") long: Double,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,weather_code",
        @Query("past_days") pastDays: Int = 7,
        @Query("forecast_days") forecastDays: Int = 1,
        @Query("timezone") timezone: String = "auto",
    ): JsonObject
}
