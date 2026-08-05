package com.quangkhai.getgo_application

import android.app.Application
import com.quangkhai.getgo_application.data.network.client.GooglePlacesClient

class GetGoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GooglePlacesClient.init(this)
    }
}
