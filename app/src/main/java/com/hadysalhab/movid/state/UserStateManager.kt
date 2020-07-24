package com.hadysalhab.movid.state

import com.hadysalhab.movid.persistence.SharedPreferencesManager

class UserStateManager(
    private val sharedPreferencesManager: SharedPreferencesManager
) {
    val isAuthenticated
        get() = sharedPreferencesManager.getStoredSessionId().isNotEmpty()
}
