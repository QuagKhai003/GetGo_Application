package com.quangkhai.getgo_application.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Bill(

    var id: String? = null,

    val name: String,

    val amount: Double,

    val paidBy: kotlin.collections.Map<String, Double>,

    val splitEqually: Boolean,     // true = split the amount equally among participants; false = use customShares

    val participants: List<String>,

    val customShares: kotlin.collections.Map<String, Double> = emptyMap(), // used when splitEqually is false: name -> the amount that person owes

    // where the bill happened (set when added from a map place sheet)
    val location: Location? = null,

    // dd-MM-yyyy
    val date: String = ""
)
