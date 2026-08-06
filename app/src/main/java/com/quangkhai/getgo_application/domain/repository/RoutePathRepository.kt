package com.quangkhai.getgo_application.domain.repository

// a driving route as an ordered list of (lat, long) points along the road
interface RoutePathRepository {
    suspend fun getRoute(
        fromLat: Double,
        fromLong: Double,
        toLat: Double,
        toLong: Double
    ): Result<List<Pair<Double, Double>>>
}
