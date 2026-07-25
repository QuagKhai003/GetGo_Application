package com.quangkhai.getgo_application.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Category (

    var id: String? = null,

    val name: String,

    val locations: List<String>? = null

)
