package com.quangkhai.getgo_application.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User (

    var id: String? = null,

    var name: String,

    var username: String,

    var password: String,

    val myLocations: List<Location> = emptyList(),

    val friends: List<Friend> = emptyList(),

    val favorites: List<Location> = emptyList()

)
