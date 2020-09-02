package com.hadysalhab.movid.account.usecases.session

import com.hadysalhab.movid.account.UserStateManager
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.datavalidator.DataValidator

class GetSessionIdUseCaseSync(
    private val userStateManager: UserStateManager,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val dataValidator: DataValidator
) {
    fun getSessionIdUseCaseSync(): String {
        val storeSessionId = userStateManager.getSessionId()
        return if (dataValidator.isSessionIdValid(storeSessionId)) {
            storeSessionId!!
        } else {
            val prefSessionId = sharedPreferencesManager.getStoredSessionId()
            userStateManager.updateSessionId(prefSessionId)
            prefSessionId
        }
    }
}