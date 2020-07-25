package com.hadysalhab.movid.state

import com.hadysalhab.movid.persistence.SharedPreferencesManager

/**
 * Class that holds information on the user state
 * */
class UserStateManager(
    private val sharedPreferencesManager: SharedPreferencesManager
) {
    val isAuthenticated
        get() = sharedPreferencesManager.getStoredSessionId().isNotEmpty()
}
