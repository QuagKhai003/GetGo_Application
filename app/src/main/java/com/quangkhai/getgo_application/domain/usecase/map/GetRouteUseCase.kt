package com.quangkhai.getgo_application.domain.usecase.map

import com.quangkhai.getgo_application.domain.repository.RoutePathRepository

class GetRouteUseCase(private val routeRepository: RoutePathRepository) {
    suspend operator fun invoke(
        fromLat: Double,
        fromLong: Double,
        toLat: Double,
        toLong: Double
    ): Result<List<Pair<Double, Double>>> =
        routeRepository.getRoute(fromLat, fromLong, toLat, toLong)
}
