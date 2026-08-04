package com.quangkhai.getgo_application.domain.usecase.map

import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.repository.MapRepository

// forward: an address or place name -> the matching places
class SearchByAddressUseCase(private val mapRepository: MapRepository) {

    suspend operator fun invoke(address: String, viewbox: String? = null): Result<List<Location>> {
        val trimmed = address.trim()
        if (trimmed.isBlank()) {
            return Result.success(emptyList())
        }
        return mapRepository.searchLocationsByAddress(trimmed, viewbox)
    }
}
