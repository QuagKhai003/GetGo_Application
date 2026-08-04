package com.quangkhai.getgo_application.data.local

import android.os.Build
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.util.Locale
import java.util.concurrent.TimeUnit

class UserAgentInterceptor(appName: String, appVersion: String) : Interceptor {
    private val userAgent: String = String.format(
        Locale.US,
        "%s/%s (Android %s; %s; %s %s; %s)",
        appName,
        appVersion,
        Build.VERSION.RELEASE,
        Build.MODEL,
        Build.BRAND,
        Build.DEVICE,
        Locale.getDefault().language
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val userAgentRequest = chain.request()
            .newBuilder()
            .header("User-Agent", userAgent)
            .build()
        return chain.proceed(userAgentRequest)
    }

    fun createClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(this)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .build()
    }
}
