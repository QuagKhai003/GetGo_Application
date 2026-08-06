package com.quangkhai.getgo_application.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quangkhai.getgo_application.data.repository.map.GoogleMapRepositoryImpl
import com.quangkhai.getgo_application.data.repository.route.GoogleRoutePathRepositoryImpl
import com.quangkhai.getgo_application.data.repository.WeatherRepositoryImpl
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.Weather
import com.quangkhai.getgo_application.domain.usecase.map.GetRouteUseCase
import com.quangkhai.getgo_application.domain.usecase.map.SearchByAddressUseCase
import com.quangkhai.getgo_application.domain.usecase.map.SearchByCoordinateUseCase
import com.quangkhai.getgo_application.domain.usecase.weather.GetWeatherUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class MapViewModel : ViewModel() {

    // Map Repository Implementation
    private val mapRepository = GoogleMapRepositoryImpl() // was MapOpenStreetRepositoryImpl()
    private val searchByAddressUseCase = SearchByAddressUseCase(mapRepository)
    private val searchByCoordinateUseCase = SearchByCoordinateUseCase(mapRepository)

    // Search response from two use case search by address or coordinates
    private val _searchResults = MutableStateFlow<List<Location>>(emptyList())
    val searchResults: StateFlow<List<Location>> = _searchResults.asStateFlow()

    // Weather Repository Implementation
    private val weatherRepository = WeatherRepositoryImpl()
    private val getWeatherUseCase = GetWeatherUseCase(weatherRepository)

    // Weather response from usecase
    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather.asStateFlow()

    // Routing Repository Implementation
    private val routeRepository = GoogleRoutePathRepositoryImpl() // was OpenStreetRoutePathRepositoryImpl()
    private val getRouteUseCase = GetRouteUseCase(routeRepository)

    // the road route to draw (current location -> destination)
    private val _routePath = MutableStateFlow<List<Pair<Double, Double>>>(emptyList())
    val routePath: StateFlow<List<Pair<Double, Double>>> = _routePath.asStateFlow()


    private val _pickedLocation = MutableStateFlow<Location?>(null)
    val pickedLocation: StateFlow<Location?> = _pickedLocation.asStateFlow()


    // what the user is currently typing
    private val _query = MutableStateFlow("")

    // set the placeholder equal to a picked location result and no re-open the list
    private var suppressNextSearch = false

    // the user's region (from device location); used to prefer nearby results
    private var region: Pair<Double, Double>? = null

    fun setRegion(lat: Double, long: Double) {
        region = lat to long
    }

    // Nominatim viewbox around the region: left,top,right,bottom = lonMin,latMax,lonMax,latMin
    private fun regionViewbox(): String? = region?.let { (lat, lon) ->
        "${lon - 0.4},${lat + 0.4},${lon + 0.4},${lat - 0.4}"
    }

    // Claude Opus 4.8 Generated Code to detect a stop from user to send search location request
    init {
        // live search: run a moment AFTER the user stops typing, so ~1 request/second limit is not hit
        viewModelScope.launch {
            _query
                .debounce(300.milliseconds)
                .map { it.trim() }
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (suppressNextSearch) {
                        suppressNextSearch = false
                    } else {
                        runSearch(query)
                    }
                }
        }
    }

    // called on every keystroke to change the text on search bar (live search)
    fun onQueryChange(text: String) {
        _query.value = text
    }

    // immediate search when get enter from android keyboard
    fun search(query: String) {
        viewModelScope.launch { runSearch(query) }
    }

    private suspend fun runSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        val coordinate = parseCoordinate(trimmed)
        val result = if (coordinate != null) {
            // coordinates -> one place, wrapped so the caller always gets a list
            searchByCoordinateUseCase(coordinate.first, coordinate.second).map { listOf(it) }
        } else {
            // text -> many candidates, preferring the user's region
            searchByAddressUseCase(trimmed, regionViewbox())
        }

        if (result.isSuccess) {
            _searchResults.value = result.getOrNull() ?: emptyList()
        } else {
            _searchResults.value = emptyList()
        }
    }

    // user tapped a result in the list
    fun pick(location: Location) {
        _pickedLocation.value = location
        _searchResults.value = emptyList()   // hide the result list once one is chosen
        suppressNextSearch = true
        fetchWeather(location.lat, location.long)
    }

    // user tapped a discovered location marker
    // map the location coordinate to real name on map
    fun pickDiscoveredPlace(place: Location) {
        _pickedLocation.value = place            // show immediately with the POI name
        fetchWeather(place.lat, place.long)
        viewModelScope.launch {
            val resolved = searchByCoordinateUseCase(place.lat, place.long).getOrNull()
            if (resolved != null) {
                // a tapped point (no id) has a placeholder name -> take the resolved name; POIs keep theirs
                val name = if (place.id == null) resolved.name.ifBlank { place.name } else place.name
                _pickedLocation.value = place.copy(
                    name = name,
                    address = resolved.address.ifBlank { resolved.name }
                )
            }
        }
    }

    // remove the pin (from the info window's ✕) — clears the sheet too
    fun clearPicked() {
        _pickedLocation.value = null
        _weather.value = null
    }

    // fetch + draw the driving route from current location to a destination
    fun fetchRoute(fromLat: Double, fromLong: Double, toLat: Double, toLong: Double) {
        viewModelScope.launch {
            _routePath.value = getRouteUseCase(fromLat, fromLong, toLat, toLong).getOrNull() ?: emptyList()
        }
    }

    fun clearRoute() {
        _routePath.value = emptyList()
    }

    // User drags the pin and then it convert the coordinate to address by searchByCoordinateUseCase
    fun pinAt(lat: Double, long: Double) {
        viewModelScope.launch {
            val result = searchByCoordinateUseCase(lat, long)
            if (result.isSuccess) {
                _pickedLocation.value = result.getOrNull()
                fetchWeather(lat, long)
            }
        }
    }

    // fetch current weather for a coordinate; silent on failure
    private fun fetchWeather(lat: Double, long: Double) {
        viewModelScope.launch {
            _weather.value = getWeatherUseCase(lat, long).getOrNull()
        }
    }

    // Claude Opus 4.8 Generated Code - Help me convert the string from the search to coordinates
    // Example: "10.772, 106.698" -> (10.772, 106.698); null when the text is not a coordinate
    private fun parseCoordinate(input: String): Pair<Double, Double>? {
        val parts = input.split(",", " ").filter { it.isNotBlank() }
        if (parts.size != 2) return null

        val lat = parts[0].trim().toDoubleOrNull() ?: return null
        val long = parts[1].trim().toDoubleOrNull() ?: return null

        if (lat !in -90.0..90.0 || long !in -180.0..180.0) return null
        return lat to long
    }
}
