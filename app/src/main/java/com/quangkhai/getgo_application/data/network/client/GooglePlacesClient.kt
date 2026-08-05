package com.quangkhai.getgo_application.data.network.client

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.quangkhai.getgo_application.BuildConfig

// Holds the Google Places SDK client. Initialised once from the Application.
object GooglePlacesClient {
    lateinit var placesClient: PlacesClient
        private set

    fun init(context: Context) {
        val app = context.applicationContext
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(app, BuildConfig.GOOGLE_PLACES_API_KEY)
        }
        placesClient = Places.createClient(app)
    }
}
