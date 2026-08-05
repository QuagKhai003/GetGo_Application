package com.quangkhai.getgo_application.domain.repository

import com.quangkhai.getgo_application.domain.model.Fact

interface FactRepository {
    // a random "Did you know?" fact + place within 15km of the given point (null if none)
    suspend fun getRandomFact(lat: Double, long: Double): Result<Fact?>
}
