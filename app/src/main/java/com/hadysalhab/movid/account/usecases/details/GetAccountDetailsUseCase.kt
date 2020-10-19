package com.hadysalhab.movid.account.usecases.details

import com.hadysalhab.movid.account.AccountResponse
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class GetAccountDetailsUseCase
    (
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val getAccountDetailsUseCaseSync: GetAccountDetailsUseCaseSync
) : BaseBusyObservable<GetAccountDetailsUseCase.Listener>() {
    interface Listener {
        fun getAccountDetailsSuccess(accountDetail: AccountResponse)
    }

    fun getAccountDetailsUseCase() {
        assertNotBusyAndBecomeBusy()
        backgroundThreadPoster.post {
            val accountResponse = getAccountDetailsUseCaseSync.getAccountDetailsUseCaseSync()
            uiThreadPoster.post {
                listeners.forEach {
                    it.getAccountDetailsSuccess(accountResponse)
                }
                becomeNotBusy()
            }
        }
    }
}