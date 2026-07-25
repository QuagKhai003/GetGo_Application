package com.quangkhai.getgo_application.domain.usecase.map

import com.quangkhai.getgo_application.domain.model.Location
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class CalculateFairSpotUseCase {

    /**
     * Finds the center location that minimizes distance variance for all participants.
     */
    operator fun invoke(startingLocations: List<Location>): Location? {
        if (startingLocations.isEmpty()) return null

        var totalLat = 0.0
        var totalLong = 0.0

        for (loc in startingLocations) {
            totalLat += loc.lat
            totalLong += loc.long
        }

        val centerLat = totalLat / startingLocations.size
        val centerLong = totalLong / startingLocations.size

        return Location(
            id = "fair_center",
            name = "Fair Meeting Spot",
            lat = centerLat,
            long = centerLong
        )
    }

    /**
     * Haversine formula: Calculates exact straight-line distance (in kilometers) between two coordinates.
     */
    fun calculateDistanceKm(loc1: Location, loc2: Location): Double {
        val r = 6371.0 // Earth radius in kilometers
        val dLat = Math.toRadians(loc2.lat - loc1.lat)
        val dLon = Math.toRadians(loc2.long - loc1.long)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(loc1.lat)) * cos(Math.toRadians(loc2.lat)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}