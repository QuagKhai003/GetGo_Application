package com.quangkhai.getgo_application.domain.model

import kotlinx.serialization.Serializable

// a group = the people sharing costs (manual names) + the bills between them
@Serializable
data class BillGroup(
    var id: String? = null,
    val name: String,
    val people: List<String>,
    val bills: List<Bill> = emptyList()
)
