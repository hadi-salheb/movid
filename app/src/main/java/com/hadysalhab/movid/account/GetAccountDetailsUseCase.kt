package com.hadysalhab.movid.account

import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.persistence.AccountDao
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class GetAccountDetailsUseCase(
    private val accountDao: AccountDao,
    private val userStateManager: UserStateManager,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val dataValidator: DataValidator
) : BaseBusyObservable<GetAccountDetailsUseCase.Listener>() {
    interface Listener {
        fun onAccountDataLoaded(accountResponse: AccountResponse)
    }

    fun getAccountDetailsUseCase() {
        assertNotBusyAndBecomeBusy()
        backgroundThreadPoster.post {
            val storeAccountResponse = userStateManager.getAccountResponse()
            if (dataValidator.isAccountResponseValid(storeAccountResponse)) {
                notify(storeAccountResponse!!)
            } else {
                val dbAccountResponse = accountDao.getAccount()
                userStateManager.updateAccountResponse(dbAccountResponse)
                if (dbAccountResponse == null) {
                    //when user login, the database is filled with the account data!!
                    throw RuntimeException("Database Account Response, not supposed to be null!!")
                }
                notify(dbAccountResponse)
            }
        }
    }

    private fun notify(accountResponse: AccountResponse) {
        uiThreadPoster.post {
            listeners.forEach {
                it.onAccountDataLoaded(accountResponse)
            }
        }
    }
}