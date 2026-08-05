package com.quangkhai.getgo_application.domain.usecase.fact

import com.quangkhai.getgo_application.domain.model.Fact
import com.quangkhai.getgo_application.domain.repository.FactRepository

class GetRandomFactUseCase(private val factRepository: FactRepository) {
    suspend operator fun invoke(lat: Double, long: Double): Result<Fact?> =
        factRepository.getRandomFact(lat, long)
}
