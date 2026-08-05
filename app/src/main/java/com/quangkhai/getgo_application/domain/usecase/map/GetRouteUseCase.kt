package com.quangkhai.getgo_application.domain.usecase.map

import com.quangkhai.getgo_application.domain.repository.LocationPathRouteRepository

class GetRouteUseCase(private val routeRepository: LocationPathRouteRepository) {
    suspend operator fun invoke(
        fromLat: Double,
        fromLong: Double,
        toLat: Double,
        toLong: Double
    ): Result<List<Pair<Double, Double>>> =
        routeRepository.getRoute(fromLat, fromLong, toLat, toLong)
}
