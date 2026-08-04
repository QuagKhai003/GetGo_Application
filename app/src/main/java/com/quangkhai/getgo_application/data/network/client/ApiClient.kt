package com.quangkhai.getgo_application.data.network.client

import com.quangkhai.getgo_application.data.network.UserApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object ApiClient {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val userApi: UserApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3004/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(UserApi::class.java)
    }

}