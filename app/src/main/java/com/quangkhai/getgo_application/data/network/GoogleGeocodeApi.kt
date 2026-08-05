package com.quangkhai.getgo_application.data.network

import kotlinx.serialization.json.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleGeocodeApi {

    // Google Geocoding API. Forward: pass address. Reverse: pass latlng ("lat,lng").
    // Whole response returned raw; the repository reads its "results" array.
    @GET("maps/api/geocode/json")
    suspend fun geocode(
        @Query("address") address: String? = null,
        @Query("latlng") latlng: String? = null,
        @Query("key") key: String,
    ): JsonObject
}
