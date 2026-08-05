package com.quangkhai.getgo_application.data.network

import kotlinx.serialization.json.JsonObject
import retrofit2.http.GET
import retrofit2.http.Path

interface WikipediaApi {

    // page summary; the "extract" field is a plain-text intro paragraph (the fun/historical bit)
    @GET("api/rest_v1/page/summary/{title}")
    suspend fun summary(@Path("title") title: String): JsonObject
}
