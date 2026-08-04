package com.quangkhai.getgo_application.data.network.client

import com.quangkhai.getgo_application.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object SupabaseClientApi {
    val supaClientApi = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        defaultSerializer = KotlinXSerializer(
            Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        )
    }
}