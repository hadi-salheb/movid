package com.hadysalhab.movid.common

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.screen.main.featuredgroups.ToolbarCountryItems

private const val PREF_SESSION_ID = "SESSION_ID"
private const val PREF_FEATURED_POWER_MENU_ITEM = "FEATURED_POWER_MENU_ITEM"
private const val PREFERENCE_THEME_MODE = "PREFERENCE_THEME_MODE"

enum class ThemeMode(val code: Int) {
    LIGHT(AppCompatDelegate.MODE_NIGHT_NO),
    DARK(AppCompatDelegate.MODE_NIGHT_YES)
}

/**
 * Class that controls access to/from shared preferences
 */
class SharedPreferencesManager(val context: Context) {
    private val _sessionId = MutableLiveData(getStoredSessionId())
    val sessionId: LiveData<String>
        get() = _sessionId

    private val _isDarkMode = MutableLiveData<Boolean>(getStoredThemeMode() == ThemeMode.DARK)
    val isDarkMode: LiveData<Boolean>
        get() = _isDarkMode

    private val _themeMode = MutableLiveData<Int>(getStoredThemeMode().code)
    val themeMode: LiveData<Int>
        get() = _themeMode

    fun setStoredThemeMode(themeMode: ThemeMode) {
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putInt(PREFERENCE_THEME_MODE, themeMode.code)
            .apply()
        _isDarkMode.value = themeMode == ThemeMode.DARK
        _themeMode.value = themeMode.code
    }

    fun getStoredThemeMode(): ThemeMode {
        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val themeModeCode = prefs.getInt(PREFERENCE_THEME_MODE, getSystemMode())
        return if (themeModeCode == AppCompatDelegate.MODE_NIGHT_NO) ThemeMode.LIGHT else ThemeMode.DARK
    }

    fun getStoredSessionId(): String {
        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_SESSION_ID, "")!!
    }

    fun setStoredSessionId(sessionId: String) {
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_SESSION_ID, sessionId)
            .apply()
        _sessionId.postValue(sessionId)
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

    private fun getSystemMode(): Int =
        when ((context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_YES
            Configuration.UI_MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }

}