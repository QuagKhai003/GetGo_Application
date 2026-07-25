package com.quangkhai.getgo_application.data.network

import com.quangkhai.getgo_application.domain.model.Friend
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {

    // User Routes--------------------------------
    @GET("users/{uid}")
    suspend fun getUserById(@Path("uid") uid: String) : User

    @POST("users/create")
    suspend fun createUser(@Body user: User): User

    @PATCH("users/{uid}/update")
    suspend fun updateUser(@Path("uid") uid: String, @Body user: User): Response<Unit>

    @DELETE("users/{uid}/delete")
    suspend fun deleteUser(@Path("uid") uid: String): Response<Unit>

    // Saved Location Routes-----------------------
    @GET("users/{uid}/locations")
    suspend fun getLocations(@Path("uid") uid: String): List<Location>

    @POST("users/{uid}/locations")
    suspend fun addLocation(@Path("uid") uid: String, @Body location: Location): Response<Unit>

    @PATCH("users/{uid}/locations/{lid}/update")
    suspend fun updateLocation(@Path("uid") uid: String, @Path("lid") lid: String, @Body location: Location): Response<Unit>

    @DELETE("users/{uid}/locations/{lid}/delete")
    suspend fun deleteLocation(@Path("uid") uid: String, @Path("lid") lid: String): Response<Unit>

    // Favorite Routes-----------------------------
    @GET("users/{uid}/favorites")
    suspend fun getFavorites(@Path("uid") uid: String): List<Location>

    @POST("users/{uid}/favorites")
    suspend fun addFavorite(@Path("uid") uid: String, @Body location: Location): Response<Unit>

    @DELETE("users/{uid}/favorites/{lid}/delete")
    suspend fun deleteFavorite(@Path("uid") uid: String, @Path("lid") lid: String): Response<Unit>

    // Friend Routes-------------------------------
    @GET("users/{uid}/friends")
    suspend fun getFriends(@Path("uid") uid: String): List<Friend>

    @POST("users/{uid}/friends")
    suspend fun addFriend(@Path("uid") uid: String, @Body friend: Friend): Response<Unit>

    @PATCH("users/{uid}/friends/{fid}/update")
    suspend fun updateFriendLocation(@Path("uid") uid: String, @Path("fid") fid: String, @Body friend: Friend): Response<Unit>

    @DELETE("users/{uid}/friends/{fid}/delete")
    suspend fun deleteFriend(@Path("uid") uid: String, @Path("fid") fid: String): Response<Unit>
}
