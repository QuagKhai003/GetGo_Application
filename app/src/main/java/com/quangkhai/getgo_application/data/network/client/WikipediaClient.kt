package com.quangkhai.getgo_application.data.network.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.quangkhai.getgo_application.data.local.UserAgentInterceptor
import com.quangkhai.getgo_application.data.network.WikipediaApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object WikipediaClient {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val userAgentClient = UserAgentInterceptor("GET_GO", "1.0").createClient()

    val wikipediaApi: WikipediaApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://en.wikipedia.org/")
            .client(userAgentClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(WikipediaApi::class.java)
    }
}
