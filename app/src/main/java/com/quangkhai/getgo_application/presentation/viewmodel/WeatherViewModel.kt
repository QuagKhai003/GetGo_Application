package com.quangkhai.getgo_application.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quangkhai.getgo_application.data.repository.WeatherRepositoryImpl
import com.quangkhai.getgo_application.domain.model.DayWeather
import com.quangkhai.getgo_application.domain.model.Weather
import com.quangkhai.getgo_application.domain.usecase.weather.GetWeatherHistoryUseCase
import com.quangkhai.getgo_application.domain.usecase.weather.GetWeatherUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Standalone weather holder for the menu card (current weather at a coordinate).
class WeatherViewModel : ViewModel() {

    private val weatherRepository = WeatherRepositoryImpl()
    private val getWeatherUseCase = GetWeatherUseCase(weatherRepository)
    private val getWeatherHistoryUseCase = GetWeatherHistoryUseCase(weatherRepository)

    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather.asStateFlow()

    // recent daily history (last week + today) at the last loaded coordinate
    private val _history = MutableStateFlow<List<DayWeather>>(emptyList())
    val history: StateFlow<List<DayWeather>> = _history.asStateFlow()

    fun load(lat: Double, long: Double) {
        viewModelScope.launch {
            _weather.value = getWeatherUseCase(lat, long).getOrNull()
        }
    }

    fun loadHistory(lat: Double, long: Double) {
        viewModelScope.launch {
            _history.value = getWeatherHistoryUseCase(lat, long).getOrNull() ?: emptyList()
        }
    }
}
