package com.quangkhai.getgo_application.data.network.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.quangkhai.getgo_application.data.network.LocationPathRouteApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object OpenStreetRouteClient {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val routeApi: LocationPathRouteApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://router.project-osrm.org/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(LocationPathRouteApi::class.java)
    }
}
