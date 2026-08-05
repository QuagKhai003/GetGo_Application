package com.quangkhai.getgo_application.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quangkhai.getgo_application.data.repository.CircleDiscoverRepositoryImpl
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.usecase.map.CircleDiscoverUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CircleDiscoverViewModel : ViewModel() {

    private val discoverRepository = CircleDiscoverRepositoryImpl()
    private val circleDiscoverUseCase = CircleDiscoverUseCase(discoverRepository)

    private val _discoveredPlaces = MutableStateFlow<List<Location>>(emptyList())
    val discoveredPlaces: StateFlow<List<Location>> = _discoveredPlaces.asStateFlow()

    // true while a discover request is in flight (so the UI can tell searching from empty)
    private val _discovering = MutableStateFlow(false)
    val discovering: StateFlow<Boolean> = _discovering.asStateFlow()

    // last discover error message (null when fine) - surfaced for debugging
    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    private var discoverJob: Job? = null

    // discover places matching `query` inside the circle (center + fixed radius)
    fun discover(query: String, centerLat: Double, centerLong: Double, radiusMeters: Int) {
        discoverJob?.cancel()
        discoverJob = viewModelScope.launch {
            _discovering.value = true
            _lastError.value = null
            _discoveredPlaces.value = emptyList()
            try {
                val result = circleDiscoverUseCase(query, centerLat, centerLong, radiusMeters)
                if (result.isSuccess) {
                    val places = result.getOrNull() ?: emptyList()
                    _discovering.value = false
                    // grow the list gradually instead of dumping all at once
                    for (place in places) {
                        _discoveredPlaces.value = _discoveredPlaces.value + place
                        delay(20)
                    }
                } else {
                    _lastError.value = result.exceptionOrNull()?.message ?: "discover failed"
                }
            } catch (e: Exception) {
                _lastError.value = e.message ?: e.toString()
            } finally {
                _discovering.value = false
            }
        }
    }

    fun clearDiscovered() {
        discoverJob?.cancel()
        _discoveredPlaces.value = emptyList()
        _discovering.value = false
    }
}
