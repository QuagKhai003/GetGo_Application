package com.quangkhai.getgo_application.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Friend (

    var id: String? = null,

    val name: String,

    val location: Location,

)
