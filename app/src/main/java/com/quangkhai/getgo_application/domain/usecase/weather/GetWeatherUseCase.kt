package com.quangkhai.getgo_application.domain.usecase.weather

import com.quangkhai.getgo_application.domain.model.Weather
import com.quangkhai.getgo_application.domain.repository.WeatherRepository

// Current weather at a coordinate (e.g. the calculated fair meeting spot)
class GetWeatherUseCase(private val weatherRepository: WeatherRepository) {

    suspend operator fun invoke(lat: Double, long: Double): Result<Weather> {
        if (lat !in -90.0..90.0 || long !in -180.0..180.0) {
            return Result.failure(IllegalArgumentException("Invalid coordinate"))
        }
        return weatherRepository.getCurrentWeather(lat, long)
    }
}
