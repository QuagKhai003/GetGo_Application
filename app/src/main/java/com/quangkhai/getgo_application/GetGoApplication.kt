package com.quangkhai.getgo_application

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.quangkhai.getgo_application.data.local.UserAgentInterceptor
import com.quangkhai.getgo_application.data.network.client.GooglePlacesClient

class GetGoApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        GooglePlacesClient.init(this)
    }

    // Coil image loader with a User-Agent so Wikimedia doesn't reject the fact photos (403)
    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .okHttpClient(UserAgentInterceptor("GET_GO", "1.0").createClient())
            .build()
}
