package com.hadysalhab.movid.account.usecases.details

import com.hadysalhab.movid.account.AccountResponse
import com.hadysalhab.movid.account.UserStateManager
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.persistence.AccountDao

class GetAccountDetailsUseCaseSync(
    private val accountDao: AccountDao,
    private val userStateManager: UserStateManager,
    private val dataValidator: DataValidator
) {
    fun getAccountDetailsUseCaseSync(): AccountResponse {
        val storeAccountResponse = userStateManager.getAccountResponse()
        return if (dataValidator.isAccountResponseValid(storeAccountResponse)) {
            storeAccountResponse!!
        } else {
            val dbAccountResponse = accountDao.getAccount()
            userStateManager.updateAccountResponse(dbAccountResponse)
            if (dbAccountResponse == null) {
                //when user login, the database is filled with the account data!!
                throw RuntimeException("Database Account Response, not supposed to be null!!")
            }
            dbAccountResponse
        }
    }
}