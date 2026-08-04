package com.quangkhai.getgo_application.data.repository

import com.quangkhai.getgo_application.domain.repository.RouteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.coroutines.cancellation.CancellationException

// Uses the free public OSRM router. Returns the road geometry as (lat, long) points.
class RouteRepositoryImpl : RouteRepository {

    private val client = OkHttpClient()

    override suspend fun getRoute(
        fromLat: Double,
        fromLong: Double,
        toLat: Double,
        toLong: Double
    ): Result<List<Pair<Double, Double>>> {
        return try {
            // OSRM wants long,lat order
            val url = "https://router.project-osrm.org/route/v1/driving/" +
                "$fromLong,$fromLat;$toLong,$toLat?overview=full&geometries=geojson"
            val request = Request.Builder().url(url).build()

            val body = withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use null
                    response.body?.string()
                }
            } ?: return Result.failure(Exception("Could not reach the router"))

            val routes = JSONObject(body).getJSONArray("routes")
            if (routes.length() == 0) return Result.failure(Exception("No route found"))

            val coordinates = routes.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates")
            val points = mutableListOf<Pair<Double, Double>>()
            for (i in 0 until coordinates.length()) {
                val point = coordinates.getJSONArray(i)
                val long = point.getDouble(0)
                val lat = point.getDouble(1)
                points.add(lat to long)
            }
            Result.success(points)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong getting the route"))
        }
    }
}
