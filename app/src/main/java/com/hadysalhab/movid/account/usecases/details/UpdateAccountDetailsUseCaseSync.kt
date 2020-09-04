package com.hadysalhab.movid.account.usecases.details

import com.hadysalhab.movid.account.AccountResponse
import com.hadysalhab.movid.account.UserStateManager
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.persistence.AccountDao

class UpdateAccountDetailsUseCaseSync(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val accountDao: AccountDao,
    private val userStateManager: UserStateManager
) {
    fun updateUserStateUseCaseSync(sessionId: String, accountResponse: AccountResponse) {
        saveSessionIdToSharedPref(sessionId)
        saveAccountResponseToDb(accountResponse)
        updateUserStateManager(sessionId, accountResponse)

    }

    private fun updateUserStateManager(sessionId: String, accountResponse: AccountResponse) =
        userStateManager.apply {
            updateSessionId(sessionId)
            updateAccountResponse(accountResponse)
        }

    private fun saveAccountResponseToDb(accountResponse: AccountResponse) =
        accountDao.insertAccountResponse(accountResponse)

    private fun saveSessionIdToSharedPref(sessionId: String) =
        sharedPreferencesManager.setStoredSessionId(sessionId)
}