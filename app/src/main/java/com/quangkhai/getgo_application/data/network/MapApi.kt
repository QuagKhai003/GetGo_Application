package com.quangkhai.getgo_application.data.network

import kotlinx.serialization.json.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface MapApi {
    @GET("search")     // search by text -> List of places
    suspend fun searchByAddress(
        @Query("q") address: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") details: Int = 1,
        @Query("limit") limit: Int = 5,
        // when set, Nominatim prefers results inside this box (current region)
        @Query("viewbox") viewbox: String? = null,
        @Query("bounded") bounded: Int? = null
    ): List<JsonObject>

    @GET("reverse")     // search by coordinate -> ONLY ONE place
    suspend fun searchByCoordinate(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("format") format: String = "json"
    ): JsonObject

    // Overpass: all places matching an Overpass QL query (used for circle discovery).
    // Whole response returned raw; the repository reads its "elements" array.
    @GET("api/interpreter")
    suspend fun discoverInCircle(
        @Query("data") query: String
    ): JsonObject
}
