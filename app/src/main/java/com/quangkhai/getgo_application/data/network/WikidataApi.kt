package com.quangkhai.getgo_application.data.network

import kotlinx.serialization.json.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface WikidataApi {

    // Wikidata SPARQL endpoint. Free, no key. Returns the raw results JSON;
    // the repository reads results.bindings.
    @GET("sparql")
    suspend fun query(
        @Query("query") sparql: String,
        @Query("format") format: String = "json",
    ): JsonObject
}
