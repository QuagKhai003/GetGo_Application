package com.quangkhai.getgo_application.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MapNode(

    val id: String,

    val name: String,

    val lat: Double,

    val lng: Double,

    val address: String? = null

)