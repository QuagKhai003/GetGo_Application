package com.quangkhai.getgo_application.data.repository

import com.quangkhai.getgo_application.data.network.MapApi
import com.quangkhai.getgo_application.data.network.client.OpenStreetMapClient
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.repository.MapRepository
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.coroutines.cancellation.CancellationException

class MapOpenStreetRepositoryImpl(
    private val mapApi: MapApi = OpenStreetMapClient.mapNominatimApi

) : MapRepository {

    override suspend fun searchLocationsByAddress(address: String, viewbox: String?): Result<List<Location>> {
        return try {
            val response = mapApi.searchByAddress(
                address = address,
                viewbox = viewbox,
                bounded = if (viewbox != null) 0 else null   // prefer the region, don't restrict to it
            )
            val locationsResponse = response.map { it.fromRawJsonCoordToLocation() }

            Result.success(locationsResponse)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun searchLocationByCoordinate(lat: Double, long: Double): Result<Location> {
        return try {
            val response = mapApi.searchByCoordinate(lat, long)
            val locationResponse = response.fromRawJsonCoordToLocation()

            Result.success(locationResponse)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    // Claude Opus 4.8 Generated Code
    // Help me convert the search response from nominatim to readable strings
    private fun JsonObject.fromRawJsonCoordToLocation(): Location {

        val displayName = this["display_name"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }

        val name = this["name"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
            ?: displayName
            ?: "Pinned Location"

        // display_name starts with the name (e.g. "Bảo tàng ..., 97A, Phố Đức Chính, ...").
        // Drop that leading name so the address line doesn't repeat the title.
        val address = when {
            displayName == null -> ""
            displayName == name -> ""                                    // name IS the full string
            displayName.startsWith("$name, ") -> displayName.removePrefix("$name, ")
            displayName.startsWith(name) -> displayName.removePrefix(name).trimStart(',', ' ')
            else -> displayName
        }

        return Location(
            name = name,
            lat = this["lat"]!!.jsonPrimitive.content.toDouble(),
            long = this["lon"]!!.jsonPrimitive.content.toDouble(),
            address = address
        )
    }
}