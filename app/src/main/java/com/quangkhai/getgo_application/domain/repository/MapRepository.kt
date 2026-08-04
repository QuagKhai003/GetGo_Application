package com.quangkhai.getgo_application.domain.repository

import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.MapNode

interface MapRepository {

    suspend fun searchLocationsByAddress(address: String, viewbox: String? = null): Result<List<Location>>

    suspend fun searchLocationByCoordinate(lat: Double, long: Double): Result<Location>

}