package com.quangkhai.getgo_application.data.local

import android.content.Context
import androidx.core.content.edit

// remembers the logged in user id across app restarts (SharedPreferences)
class PrefManager(context: Context) {

    private val userPrefs = context.getSharedPreferences("getgo_session", Context.MODE_PRIVATE)

    fun saveUserId(userId: String) {
        userPrefs.edit { putString("user_id", userId) }
    }

    fun getUserId(): String? = userPrefs.getString("user_id", null)

    fun saveMyLocationActive(active: Boolean) {
        userPrefs.edit { putBoolean("my_location_active", active) }
    }

    fun getMyLocationActive(): Boolean = userPrefs.getBoolean("my_location_active", true)

    // the friend ids the user has checked on the map
    fun saveCheckedFriends(ids: Set<String>) {
        userPrefs.edit { putStringSet("checked_friends", ids) }
    }

    fun getCheckedFriends(): Set<String> =
        userPrefs.getStringSet("checked_friends", emptySet()) ?: emptySet()

    fun clear() {
        userPrefs.edit { clear() }
    }
}
