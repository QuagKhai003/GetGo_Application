package com.quangkhai.getgo_application.data.network.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.quangkhai.getgo_application.data.network.GoogleGeocodeApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object GoogleGeocodeClient {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val geocodeApi: GoogleGeocodeApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GoogleGeocodeApi::class.java)
    }
}
