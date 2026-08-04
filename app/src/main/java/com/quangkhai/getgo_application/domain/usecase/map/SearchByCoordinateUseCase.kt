package com.quangkhai.getgo_application.domain.usecase.map

import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.repository.MapRepository

// reverse: a point on the map -> the place there
class SearchByCoordinateUseCase(private val mapRepository: MapRepository) {

    suspend operator fun invoke(lat: Double, long: Double): Result<Location> {
        return mapRepository.searchLocationByCoordinate(lat, long)
    }
}
