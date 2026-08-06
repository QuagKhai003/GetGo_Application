package com.quangkhai.getgo_application.data.repository.route

import com.quangkhai.getgo_application.BuildConfig
import com.quangkhai.getgo_application.data.network.GoogleDirectionsApi
import com.quangkhai.getgo_application.data.network.client.GoogleDirectionsClient
import com.quangkhai.getgo_application.domain.repository.RoutePathRepository
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.coroutines.cancellation.CancellationException

// Google Directions version of the route lookup. Same contract as the OSRM one,
// so it swaps in by changing which impl the MapViewModel constructs
class GoogleRoutePathRepositoryImpl(
    private val routeApi: GoogleDirectionsApi = GoogleDirectionsClient.routeApi
) : RoutePathRepository {

    override suspend fun getRoute(
        fromLat: Double,
        fromLong: Double,
        toLat: Double,
        toLong: Double
    ): Result<List<Pair<Double, Double>>> {
        return try {
            val response = routeApi.getRoute(
                origin = "$fromLat,$fromLong",
                destination = "$toLat,$toLong",
                key = BuildConfig.GOOGLE_PLACES_API_KEY
            )

            val routes = response["routes"]?.jsonArray
            if (routes.isNullOrEmpty()) return Result.failure(Exception("No route found"))

            // Google returns the whole road as one encoded polyline string, not raw points
            val encoded = routes[0].jsonObject["overview_polyline"]!!.jsonObject["points"]!!.jsonPrimitive.content
            Result.success(decodePolyline(encoded))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong getting the route"))
        }
    }

    // Claude Opus 4.8 Generated Code - decode Google's encoded polyline into (lat, long) points
    private fun decodePolyline(encoded: String): List<Pair<Double, Double>> {
        val points = mutableListOf<Pair<Double, Double>>()
        var index = 0
        var lat = 0
        var long = 0
        while (index < encoded.length) {
            var shift = 0
            var result = 0
            var b: Int
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            long += if (result and 1 != 0) (result shr 1).inv() else result shr 1

            points.add((lat / 1e5) to (long / 1e5))
        }
        return points
    }
}
