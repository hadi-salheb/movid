package com.hadysalhab.movid.account

import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class GetAccountDetailsUseCase(
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val getAccountDetailsUseCaseSync: GetAccountDetailsUseCaseSync
) : BaseBusyObservable<GetAccountDetailsUseCase.Listener>() {
    interface Listener {
        fun onAccountDataLoaded(accountResponse: AccountResponse)
    }

    fun getAccountDetailsUseCaseAndNotify() {
        assertNotBusyAndBecomeBusy()
        backgroundThreadPoster.post {
            val accountResponse = getAccountDetailsUseCaseSync.getAccountDetailsUseCaseSync()
            notify(accountResponse)
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