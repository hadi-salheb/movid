package com.hadysalhab.movid.common

import android.content.Context
import com.hadysalhab.movid.screen.main.featuredgroups.ToolbarCountryItems

private const val PREF_SESSION_ID = "SESSION_ID"
private const val PREF_FEATURED_POWER_MENU_ITEM = "FEATURED_POWER_MENU_ITEM"

/**
 * Class that controls access to/from shared preferences
 */
class SharedPreferencesManager(val context: Context) {
    fun getStoredSessionId(): String {
        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_SESSION_ID, "")!!
    }

    fun setStoredSessionId(sessionId: String) {
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_SESSION_ID, sessionId)
            .apply()
    }

    fun getStoredFeaturedPowerMenuItem(): ToolbarCountryItems {
        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val position = prefs.getInt(PREF_FEATURED_POWER_MENU_ITEM, 0)
        return ToolbarCountryItems.values()[position]
    }

    fun setStoredFeaturedPowerMenuItem(featuredPowerMenuItem: ToolbarCountryItems) {
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putInt(PREF_FEATURED_POWER_MENU_ITEM, featuredPowerMenuItem.ordinal)
            .apply()
    }

}