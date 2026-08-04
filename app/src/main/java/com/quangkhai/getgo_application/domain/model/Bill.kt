package com.quangkhai.getgo_application.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Bill(
    var id: String? = null,
    val name: String,
    val amount: Double,
    // who fronted the money and how much - a bill can have several payers
    // e.g. {"A": 400000.0, "B": 200000.0}
    // (fully-qualified: this package has its own domain model named Map)
    val paidBy: kotlin.collections.Map<String, Double>,
    // true = split the amount equally among participants; false = use customShares
    val splitEqually: Boolean,
    val participants: List<String>,
    // used when splitEqually is false: name -> the amount that person owes
    val customShares: kotlin.collections.Map<String, Double> = emptyMap(),
    // where the bill happened (set when added from a map place sheet)
    val location: Location? = null,
    // dd-MM-yyyy
    val date: String = ""
)
