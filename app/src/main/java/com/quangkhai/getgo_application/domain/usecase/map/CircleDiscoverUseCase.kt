package com.quangkhai.getgo_application.domain.usecase.map

import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.repository.CircleDiscoverRepository

// Selectable discovery category class for option in checklist
data class DiscoverCategory(val label: String, val term: String)

class CircleDiscoverUseCase(private val circleDiscoverRepository: CircleDiscoverRepository) {

    suspend operator fun invoke(
        query: String,
        centerLat: Double,
        centerLong: Double,
        radiusMeters: Int,
    ): Result<List<Location>> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return Result.success(emptyList())

        if (centerLat !in -90.0..90.0 || centerLong !in -180.0..180.0) {
            return Result.failure(IllegalArgumentException("Invalid center coordinate"))
        }

        // clamp radius to a sane band (Overpass around: is meters)
        val radius = radiusMeters.coerceIn(100, 5000)

        return circleDiscoverRepository.discoverPlaces(
            term = trimmed,
            centerLat = centerLat,
            centerLong = centerLong,
            radiusMeters = radius,
        )
    }


    // Claude Opus 4.8 Generated Code for getting query-able locations
    companion object {
        // The checkbox catalog shown in the discovery tooltip.
        // label = one word for the user; term = the Overpass alternation queried.
        val CATEGORIES = listOf(
            DiscoverCategory("Coffee", "cafe|coffee"),
            DiscoverCategory("Food", "restaurant|fast_food|food_court"),
            DiscoverCategory("Gym", "gym|fitness_centre|fitness"),
            DiscoverCategory("Bar", "bar|pub|biergarten"),
            DiscoverCategory("Hotel", "hotel|hostel|guest_house|motel"),
            DiscoverCategory("Bank", "atm|bank"),
            DiscoverCategory("Fuel", "fuel"),
            DiscoverCategory("Park", "park|garden"),
            DiscoverCategory("Shop", "supermarket|mall|convenience"),
            DiscoverCategory("Hospital", "hospital|clinic|pharmacy"),
            DiscoverCategory("School", "school|university|college"),
        )
    }
}
