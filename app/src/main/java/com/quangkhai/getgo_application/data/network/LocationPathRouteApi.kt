package com.quangkhai.getgo_application.data.network

import kotlinx.serialization.json.JsonObject
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LocationPathRouteApi {

    // OSRM driving route. coords is "fromLong,fromLat;toLong,toLat" (OSRM wants long,lat order).
    // Whole response returned raw; the repository reads its "routes" geometry.
    @GET("route/v1/driving/{coords}")
    suspend fun getRoute(
        @Path("coords", encoded = true) coords: String,
        @Query("overview") overview: String = "full",
        @Query("geometries") geometries: String = "geojson",
    ): JsonObject
}
