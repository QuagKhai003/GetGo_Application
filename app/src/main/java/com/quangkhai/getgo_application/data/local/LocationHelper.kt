package com.quangkhai.getgo_application.data.local

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// Check whether the app was granted with location permission
fun hasLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

/**
 * Claude Opus 4.8 Generated Code for handling google gms lirabries to get current location
 * Parse the current location as (lat, long), or null if it can't be read.
 */
@SuppressLint("MissingPermission")
suspend fun getCurrentLatLong(context: Context): Pair<Double, Double>? {
    val client = LocationServices.getFusedLocationProviderClient(context)

    val fresh = suspendCancellableCoroutine<Location?> { cont ->
        val cancellation = CancellationTokenSource()
        try {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellation.token)
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resume(null) }
        } catch (e: SecurityException) {
            cont.resume(null)
        }
        cont.invokeOnCancellation { cancellation.cancel() }
    }
    if (fresh != null) return fresh.latitude to fresh.longitude

    val last = suspendCancellableCoroutine<Location?> { cont ->
        try {
            client.lastLocation
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resume(null) }
        } catch (e: SecurityException) {
            cont.resume(null)
        }
    }
    return last?.let { it.latitude to it.longitude }
}



