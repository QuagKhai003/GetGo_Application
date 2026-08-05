package com.quangkhai.getgo_application.data.repository

import com.quangkhai.getgo_application.data.network.LocationPathRouteApi
import com.quangkhai.getgo_application.data.network.client.OpenStreetRouteClient
import com.quangkhai.getgo_application.domain.repository.LocationPathRouteRepository
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.double
import kotlin.coroutines.cancellation.CancellationException

// Uses the free public OSRM router. Returns the road geometry as (lat, long) points.
class LocationPathRouteRepositoryImpl(
    private val routeApi: LocationPathRouteApi = OpenStreetRouteClient.routeApi
) : LocationPathRouteRepository {

    override suspend fun getRoute(
        fromLat: Double,
        fromLong: Double,
        toLat: Double,
        toLong: Double
    ): Result<List<Pair<Double, Double>>> {
        return try {
            // OSRM wants long,lat order
            val coords = "$fromLong,$fromLat;$toLong,$toLat"
            val response = routeApi.getRoute(coords)

            val routes = response["routes"]?.jsonArray
            if (routes.isNullOrEmpty()) return Result.failure(Exception("No route found"))

            val coordinates = routes[0].jsonObject["geometry"]!!.jsonObject["coordinates"]!!.jsonArray
            val points = coordinates.map { entry ->
                val point = entry.jsonArray
                val long = point[0].jsonPrimitive.double
                val lat = point[1].jsonPrimitive.double
                lat to long
            }
            Result.success(points)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong getting the route"))
        }
    }
}
