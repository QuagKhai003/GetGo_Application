package com.quangkhai.getgo_application.data.network.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.quangkhai.getgo_application.data.local.UserAgentInterceptor
import com.quangkhai.getgo_application.data.network.MapApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object OpenStreetMapClient {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    // Create a User-Agent so Nominatim does not block the request
    private val userAgentForOkHttpClient = UserAgentInterceptor("GET_GO", "1.0").createClient()

    val mapNominatimApi: MapApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(userAgentForOkHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MapApi::class.java)
    }

    val mapOverpassApi: MapApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://overpass-api.de/")
            .client(userAgentForOkHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MapApi::class.java)
    }
}
