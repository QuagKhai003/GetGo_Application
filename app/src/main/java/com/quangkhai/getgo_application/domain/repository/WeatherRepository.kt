package com.quangkhai.getgo_application.domain.repository

import com.quangkhai.getgo_application.domain.model.DayWeather
import com.quangkhai.getgo_application.domain.model.Weather

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, long: Double): Result<Weather>

    suspend fun getWeatherHistory(lat: Double, long: Double): Result<List<DayWeather>>
}
