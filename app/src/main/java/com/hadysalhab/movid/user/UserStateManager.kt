package com.hadysalhab.movid.user

import com.hadysalhab.movid.common.SharedPreferencesManager

/**
 * Class that holds information on the user state
 * */
class UserStateManager(
    private val sharedPreferencesManager: SharedPreferencesManager
) {
    val isAuthenticated
        get() = sharedPreferencesManager.getStoredSessionId().isNotEmpty()
    val sessionId
        get() = sharedPreferencesManager.getStoredSessionId()
}
