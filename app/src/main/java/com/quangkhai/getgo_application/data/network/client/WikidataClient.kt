package com.quangkhai.getgo_application.data.network.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.quangkhai.getgo_application.data.local.UserAgentInterceptor
import com.quangkhai.getgo_application.data.network.WikidataApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object WikidataClient {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    // Wikidata requires a descriptive User-Agent, same as Nominatim
    private val userAgentClient = UserAgentInterceptor("GET_GO", "1.0").createClient()

    val wikidataApi: WikidataApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://query.wikidata.org/")
            .client(userAgentClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(WikidataApi::class.java)
    }
}
