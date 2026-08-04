package com.quangkhai.getgo_application.data.local

import android.content.Context

// remembers the logged in user id across app restarts (SharedPreferences)
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("getgo_session", Context.MODE_PRIVATE)

    fun saveUserId(userId: String) {
        prefs.edit().putString("user_id", userId).apply()
    }

    fun getUserId(): String? = prefs.getString("user_id", null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}
