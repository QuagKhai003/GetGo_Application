package com.quangkhai.getgo_application.domain.repository

import com.quangkhai.getgo_application.domain.model.Location

interface CircleDiscoverRepository {

    // Find all places matching selected options inside a circle.
    suspend fun discoverPlaces(
        term: String,
        centerLat: Double,
        centerLong: Double,
        radiusMeters: Int,
    ): Result<List<Location>>
}
