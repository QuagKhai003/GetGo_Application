package com.quangkhai.getgo_application.domain.model

// one "Did you know?" fact about a place, from Wikidata
data class Fact(
    val title: String,

    val description: String,

    val imageUrl: String,

    val lat: Double,

    val long: Double,
)
