package com.quangkhai.getgo_application.domain.usecase.map

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// Claude Opus 4.8 generated code for calculating a distance between two coordinates in metres
fun haversineMeters(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
    val earthRadius = 6371000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLong = Math.toRadians(long2 - long1)
    val a = sin(dLat / 2).pow(2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLong / 2).pow(2)
    return 2 * earthRadius * asin(sqrt(a))
}

// Claude Opus 4.8 generated code for calculating a fair centre point of whole list friend
// the fair meeting centre = centre of the smallest circle that encloses everyone
// computed on lat/long in a metre-scaled
// plane (longitude * cos(lat)). Uses the Badoiu-Clarkson iterative shrinking method,
fun fairCenter(points: List<Pair<Double, Double>>): Pair<Double, Double> {
    if (points.isEmpty()) return 0.0 to 0.0
    if (points.size == 1) return points[0]

    val refLat = points.map { it.first }.average()
    val cosRef = cos(Math.toRadians(refLat))
    val xs = points.map { it.second * cosRef }   // planar x (metre-proportional)
    val ys = points.map { it.first }             // planar y
    val n = points.size

    // start at the centroid, then repeatedly step a shrinking amount toward the
    // farthest member; this converges onto the minimum-enclosing-circle centre
    var cx = xs.average()
    var cy = ys.average()
    for (t in 0 until 2000) {
        var farthest = 0
        var farthestDist = -1.0
        for (i in 0 until n) {
            val dx = xs[i] - cx
            val dy = ys[i] - cy
            val dist = dx * dx + dy * dy
            if (dist > farthestDist) {
                farthestDist = dist
                farthest = i
            }
        }
        val step = 1.0 / (t + 2)
        cx += (xs[farthest] - cx) * step
        cy += (ys[farthest] - cy) * step
    }

    return cy to (cx / cosRef)
}
