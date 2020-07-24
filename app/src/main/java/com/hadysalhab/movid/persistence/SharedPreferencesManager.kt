package com.hadysalhab.movid.persistence

import android.content.Context

private const val PREF_SESSION_ID = "SESSION_ID"

class SharedPreferencesManager(val context: Context) {
    fun getStoredSessionId(): String {
        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_SESSION_ID, "")!!
    }

    fun setStoredSessionId(sessionId: String){
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_SESSION_ID, sessionId)
            .apply()
    }

}