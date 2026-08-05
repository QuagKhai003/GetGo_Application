package com.quangkhai.getgo_application.data.repository

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.quangkhai.getgo_application.data.network.client.GooglePlacesClient
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.repository.MagicCircleDiscoverRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


// Google Places SDK (New) Nearby Search version of discovery. Returns places of the given
class GooglePlacesDiscoverRepositoryImpl(
    private val placesClient: PlacesClient = GooglePlacesClient.placesClient
) : MagicCircleDiscoverRepository {

    // constraint needed field to request - keeps the cheaper billing
    private val fields = listOf(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.LAT_LNG,
        Place.Field.ADDRESS,
    )

    // Each UI category is a group of OSM tokens. Every token in a group maps to that
    // group's FULL set of Google place types, so picking a category searches them all.
    private val osmToGoogleTypes = mapOf(
        // Coffee
        "cafe" to listOf("cafe", "coffee_shop"),
        "coffee" to listOf("cafe", "coffee_shop"),
        // Food
        "restaurant" to listOf("restaurant", "fast_food_restaurant", "meal_takeaway"),
        "fast_food" to listOf("restaurant", "fast_food_restaurant", "meal_takeaway"),
        "food_court" to listOf("restaurant", "fast_food_restaurant", "meal_takeaway"),
        // Gym
        "gym" to listOf("gym", "fitness_center"),
        "fitness_centre" to listOf("gym", "fitness_center"),
        "fitness" to listOf("gym", "fitness_center"),
        // Bar
        "bar" to listOf("bar", "pub"),
        "pub" to listOf("bar", "pub"),
        "biergarten" to listOf("bar", "pub"),
        // Hotel
        "hotel" to listOf("hotel", "motel", "hostel", "guest_house"),
        "hostel" to listOf("hotel", "motel", "hostel", "guest_house"),
        "guest_house" to listOf("hotel", "motel", "hostel", "guest_house"),
        "motel" to listOf("hotel", "motel", "hostel", "guest_house"),
        // Bank
        "bank" to listOf("bank", "atm"),
        "atm" to listOf("bank", "atm"),
        // Fuel
        "fuel" to listOf("gas_station"),
        // Park
        "park" to listOf("park"),
        "garden" to listOf("park"),
    )


    // Claude Opus 4.8 generated code similar to OSM
    override suspend fun discoverPlaces(
        term: String,
        centerLat: Double,
        centerLong: Double,
        radiusMeters: Int,
    ): Result<List<Location>> {
        // the categories arrive joined by "|"; map each to a Google place type
        val types = term.split("|").flatMap { osmToGoogleTypes[it.trim()] ?: emptyList() }.distinct()
        // guard: nothing to search -> no paid request
        if (types.isEmpty()) return Result.success(emptyList())

        return try {
            val request = SearchNearbyRequest.builder(
                CircularBounds.newInstance(LatLng(centerLat, centerLong), radiusMeters.toDouble()),
                fields
            )
                .setIncludedTypes(types)
                .setMaxResultCount(20)
                .build()

            val response = placesClient.searchNearby(request).await()
            val list = response.places.mapNotNull { it.toLocationOrNull() }
            Result.success(list)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    // one Places SDK result -> Location
    private fun Place.toLocationOrNull(): Location? {
        val latLng = latLng ?: return null
        return Location(
            id = id,
            name = name ?: "Unnamed place",
            lat = latLng.latitude,
            long = latLng.longitude,
            address = address ?: "",
        )
    }

    // await a Play-services Task without the extra coroutines-play-services dependency
    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resumeWithException(it) }
    }
}
