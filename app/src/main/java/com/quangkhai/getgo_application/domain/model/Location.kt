package com.quangkhai.getgo_application.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Location (

    var id: String? = null,

    var name: String,

    var lat: Double,

    var long: Double,

    var address: String = "",

)
