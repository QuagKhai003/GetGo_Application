package com.quangkhai.getgo_application.data.network

import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.User
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MapApi {
    @GET("map")
    suspend fun loadMap(): List<Location>

}