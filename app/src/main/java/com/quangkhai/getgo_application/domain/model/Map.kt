package com.quangkhai.getgo_application.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Map (

    val id: String,

    val category: List<Category>?

)