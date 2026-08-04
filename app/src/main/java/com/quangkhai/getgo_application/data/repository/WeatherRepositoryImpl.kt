package com.quangkhai.getgo_application.data.repository

import com.quangkhai.getgo_application.data.network.WeatherApi
import com.quangkhai.getgo_application.data.network.client.OpenMeteoClient
import com.quangkhai.getgo_application.domain.model.Weather
import com.quangkhai.getgo_application.domain.repository.WeatherRepository
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.coroutines.cancellation.CancellationException

class WeatherRepositoryImpl(
    private val weatherApi: WeatherApi = OpenMeteoClient.weatherApi

) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, long: Double): Result<Weather> {
        return try {
            val response = weatherApi.getCurrentWeather(lat, long)
            Result.success(response.toWeather())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    // Convert Json raw response into Weather object
    private fun JsonObject.toWeather(): Weather {
        val current = this["current"]?.jsonObject
        val code = current?.get("weather_code")?.jsonPrimitive?.intOrNull ?: -1

        return Weather(
            temperatureC = current?.get("temperature_2m")?.jsonPrimitive?.doubleOrNull ?: 0.0,
            humidity = current?.get("relative_humidity_2m")?.jsonPrimitive?.intOrNull ?: 0,
            windSpeedKmh = current?.get("wind_speed_10m")?.jsonPrimitive?.doubleOrNull ?: 0.0,
            weatherCode = code,
            description = describeWeatherCode(code),
        )
    }

    // Claude Opus 4.8 generated code
    // Maps WMO weather codes to a short label.
    // Reference: https://open-meteo.com/en/docs#weather_variable_documentation
    private fun describeWeatherCode(code: Int): String = when (code) {
        0 -> "Clear sky"
        1, 2, 3 -> "Partly cloudy"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        56, 57 -> "Freezing drizzle"
        61, 63, 65 -> "Rain"
        66, 67 -> "Freezing rain"
        71, 73, 75 -> "Snow"
        77 -> "Snow grains"
        80, 81, 82 -> "Rain showers"
        85, 86 -> "Snow showers"
        95 -> "Thunderstorm"
        96, 99 -> "Thunderstorm with hail"
        else -> "Unknown"
    }
}
