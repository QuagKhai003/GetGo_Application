package com.quangkhai.getgo_application.domain.usecase.weather

import com.quangkhai.getgo_application.domain.model.DayWeather
import com.quangkhai.getgo_application.domain.repository.WeatherRepository

class GetWeatherHistoryUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(lat: Double, long: Double): Result<List<DayWeather>> =
        weatherRepository.getWeatherHistory(lat, long)
}
