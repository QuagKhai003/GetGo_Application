package com.quangkhai.getgo_application.data.local

import android.content.Context
import androidx.core.content.edit

// remembers the logged in user id across app restarts (SharedPreferences)
class SessionManager(context: Context) {

    private val userSession = context.getSharedPreferences("getgo_session", Context.MODE_PRIVATE)

    fun saveUserId(userId: String) {
        userSession.edit { putString("user_id", userId) }
    }

    fun getUserId(): String? = userSession.getString("user_id", null)

    fun clear() {
        userSession.edit { clear() }
    }
}
