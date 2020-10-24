package com.hadysalhab.movid.common

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.screen.main.featuredgroups.ToolbarCountryItems

private const val PREF_SESSION_ID = "SESSION_ID"
private const val PREF_FEATURED_POWER_MENU_ITEM = "FEATURED_POWER_MENU_ITEM"

private const val PREFERENCE_NIGHT_MODE = "preference_night_mode"
private const val PREFERENCE_NIGHT_MODE_DEFAULT_VALUE = AppCompatDelegate.MODE_NIGHT_NO

/**
 * Class that controls access to/from shared preferences
 */
class SharedPreferencesManager(val context: Context) {


    private val nightMode: Int
        get() = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(PREFERENCE_NIGHT_MODE, PREFERENCE_NIGHT_MODE_DEFAULT_VALUE)
    var isDarkTheme: Boolean = false
        get() = nightMode == AppCompatDelegate.MODE_NIGHT_YES
        set(value) {
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putInt(
                    PREFERENCE_NIGHT_MODE, if (value) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_NO
                    }
                ).apply()
            field = value
        }

    private val _isDarkThemeLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isDarkThemeLiveData: LiveData<Boolean>
        get() = _isDarkThemeLiveData

    private val _nightModeLiveData: MutableLiveData<Int> = MutableLiveData()
    val nightModeLiveData: LiveData<Int>
        get() = _nightModeLiveData


    private val _sessionId = MutableLiveData(getStoredSessionId())
    val sessionId: LiveData<String>
        get() = _sessionId

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

    private val preferenceChangedListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                PREFERENCE_NIGHT_MODE -> {
                    _nightModeLiveData.value = nightMode
                    _isDarkThemeLiveData.value = isDarkTheme
                }
            }
        }

    init {
        _isDarkThemeLiveData.value = isDarkTheme
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener(preferenceChangedListener)
    }

}