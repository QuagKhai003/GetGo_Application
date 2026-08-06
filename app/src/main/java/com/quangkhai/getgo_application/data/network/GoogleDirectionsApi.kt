package com.quangkhai.getgo_application.data.network

import kotlinx.serialization.json.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleDirectionsApi {

    // Google Directions API driving route
    // Whole response returned raw; the repository decodes routes[0].overview_polyline.
    @GET("maps/api/directions/json")
    suspend fun getRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String,
    ): JsonObject
}
