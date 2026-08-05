package com.quangkhai.getgo_application.data.repository.map

import com.quangkhai.getgo_application.BuildConfig
import com.quangkhai.getgo_application.data.network.GoogleGeocodeApi
import com.quangkhai.getgo_application.data.network.client.GoogleGeocodeClient
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.repository.MapRepository
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.coroutines.cancellation.CancellationException

// Google Geocoding version of address/coordinate lookup. Same contract as the OSM one,
// so it swaps in by changing which impl the MapViewModel constructs. Paid + needs a key.
class GoogleMapRepositoryImpl(
    private val geocodeApi: GoogleGeocodeApi = GoogleGeocodeClient.geocodeApi
) : MapRepository {

    override suspend fun searchLocationsByAddress(address: String, viewbox: String?): Result<List<Location>> {
        return try {
            val response = geocodeApi.geocode(address = address, key = BuildConfig.GOOGLE_PLACES_API_KEY)
            val results = response["results"]?.jsonArray ?: return Result.success(emptyList())
            Result.success(results.mapNotNull { it.jsonObject.toLocationOrNull() })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun searchLocationByCoordinate(lat: Double, long: Double): Result<Location> {
        return try {
            val response = geocodeApi.geocode(latlng = "$lat,$long", key = BuildConfig.GOOGLE_PLACES_API_KEY)
            val place = response["results"]?.jsonArray?.firstOrNull()?.jsonObject?.toLocationOrNull()
                ?: return Result.failure(Exception("No place found"))
            Result.success(place)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    // one Google Geocoding result -> Location
    private fun JsonObject.toLocationOrNull(): Location? {
        val location = this["geometry"]?.jsonObject?.get("location")?.jsonObject ?: return null
        val lat = location["lat"]?.jsonPrimitive?.doubleOrNull ?: return null
        val long = location["lng"]?.jsonPrimitive?.doubleOrNull ?: return null

        val address = this["formatted_address"]?.jsonPrimitive?.contentOrNull ?: ""
        // Geocoding has no POI name; use the first part of the address as the short name
        val name = address.substringBefore(",").ifBlank { address }
        val id = this["place_id"]?.jsonPrimitive?.contentOrNull

        return Location(id = id, name = name, lat = lat, long = long, address = address)
    }
}
