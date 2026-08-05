package com.quangkhai.getgo_application.data.repository.circlediscovery

import com.quangkhai.getgo_application.data.network.MapApi
import com.quangkhai.getgo_application.data.network.client.OpenStreetMapClient
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.repository.MagicCircleDiscoverRepository
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class MagicCircleDiscoverRepositoryImpl(
    private val mapApi: MapApi = OpenStreetMapClient.mapOverpassApi

) : MagicCircleDiscoverRepository {
    private val searchKeys = listOf("amenity", "shop", "leisure")

    // cap on returned elements so a dense area does not flood the map
    private val resultLimit = 20

    override suspend fun discoverPlaces(
        term: String,
        centerLat: Double,
        centerLong: Double,
        radiusMeters: Int,
    ): Result<List<Location>> {
        val safeTerm = sanitizeTerm(term)
        if (safeTerm.isBlank()) return Result.success(emptyList())

        return try {
            val query = buildOverpassQuery(safeTerm, centerLat, centerLong, radiusMeters)
            val elements = mapApi.discoverInCircle(query)["elements"]?.jsonArray

            if (elements != null) {
                val places = elements.toPlaces(centerLat, centerLong)
                Result.success(places)
            } else {
                Result.success(emptyList())
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    // parse elements into places, drop duplicates, sort nearest-first
    private fun JsonArray.toPlaces(centerLat: Double, centerLong: Double): List<Location> =
        mapNotNull { it.jsonObject.toLocationOrNull() }
            .distinctBy { "${it.name}|${round5Decimal(it.lat)}|${round5Decimal(it.long)}" }
            .sortedBy { distanceKm(centerLat, centerLong, it.lat, it.long) }

    // Claude Opus 4.8 generated code
    // Strip anything that could break out of the QL string / regex literal.
    private fun sanitizeTerm(term: String): String =
        term.filter { it.isLetterOrDigit() || it == ' ' || it == '-' || it == '|' }
            .trim()

    // Claude Opus 4.8 generated code for build Overpass API query
    private fun buildOverpassQuery(
        term: String,
        lat: Double,
        long: Double,
        radiusMeters: Int,
    ): String {
        val around = "around:$radiusMeters,$lat,$long"
        val clauses = searchKeys.joinToString("\n") { key ->
            "  nwr($around)[\"$key\"~\"$term\",i];"
        }
        return """
            [out:json][timeout:25];
            (
            $clauses
            );
            out center $resultLimit;
        """.trimIndent()
    }

    // Claude Opus 4.8 generated code for converting raw Json response from Overpass API to a location
    private fun JsonObject.toLocationOrNull(): Location? {
        val lat = this["lat"]?.jsonPrimitive?.doubleOrNull
            ?: this["center"]?.jsonObject?.get("lat")?.jsonPrimitive?.doubleOrNull
            ?: return null
        val long = this["lon"]?.jsonPrimitive?.doubleOrNull
            ?: this["center"]?.jsonObject?.get("lon")?.jsonPrimitive?.doubleOrNull
            ?: return null

        val tags = this["tags"]?.jsonObject
        val category = listOf("amenity", "shop", "cuisine", "leisure")
            .firstNotNullOfOrNull { tags?.get(it)?.jsonPrimitive?.contentOrNull }

        val name = tags?.get("name")?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
            ?: category?.replaceFirstChar { it.uppercase() }
            ?: "Unnamed place"

        // real street address from addr:* tags (house + street, then suburb/city)
        val street = listOfNotNull(
            tags?.get("addr:housenumber")?.jsonPrimitive?.contentOrNull,
            tags?.get("addr:street")?.jsonPrimitive?.contentOrNull
        ).joinToString(" ")
        val area = listOfNotNull(
            tags?.get("addr:suburb")?.jsonPrimitive?.contentOrNull,
            tags?.get("addr:city")?.jsonPrimitive?.contentOrNull
        ).joinToString(", ")
        val address = listOf(street, area).filter { it.isNotBlank() }.joinToString(", ")

        val id = this["type"]?.jsonPrimitive?.contentOrNull?.let { type ->
            "$type/${this["id"]?.jsonPrimitive?.contentOrNull}"
        }

        return Location(id, name, lat, long, address)
    }

    // collapse near-identical coordinates because slightly more change of coordiante could different data record
    private fun round5Decimal(value: Double): Double = (value * 1e5).toLong() / 1e5

    // Claude Opus 4.8 generated code for calculating straight-line distance in kilometers between two coordinates (for nearest-first sort)
    private fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}
