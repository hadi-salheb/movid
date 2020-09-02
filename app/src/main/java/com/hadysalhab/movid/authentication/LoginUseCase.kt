package com.hadysalhab.movid.authentication

import com.hadysalhab.movid.account.usecases.details.FetchAccountDetailsUseCaseSync
import com.hadysalhab.movid.authentication.createsession.CreateSessionUseCaseSync
import com.hadysalhab.movid.authentication.createtoken.CreateRequestTokenUseCaseSync
import com.hadysalhab.movid.authentication.signtoken.SignTokenUseCaseSync
import com.hadysalhab.movid.common.usecases.UseCaseSyncResults
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster


class LoginUseCase(
    private val createRequestTokenUseCaseSync: CreateRequestTokenUseCaseSync,
    private val signTokenUseCaseSync: SignTokenUseCaseSync,
    private val createSessionUseCaseSync: CreateSessionUseCaseSync,
    private val fetchAccountDetailsUseCaseSync: FetchAccountDetailsUseCaseSync,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster
) :
    BaseBusyObservable<LoginUseCase.Listener>() {
    interface Listener {
        fun onLoggedIn()
        fun onLoginFailed(msg: String)
    }

    var userName: String = ""
    var password: String = ""


    fun loginAndNotify(username: String, password: String) {
        //throw exception if clients tries to trigger this flow while it is busy
        assertNotBusyAndBecomeBusy()
        this.userName = username
        this.password = password
        backgroundThreadPoster.post {
            createRequestToken()
        }
    }

    private fun createRequestToken() {
        when (val result = createRequestTokenUseCaseSync.createRequestTokenSync()) {
            is UseCaseSyncResults.Results -> signTokenWithLogin(result.data.requestToken)
            is UseCaseSyncResults.Error -> notifyFailure(result.errMessage)
        }
    }

    private fun signTokenWithLogin(token: String) {
        when (val result = signTokenUseCaseSync.signTokenSync(
            userName,
            password,
            token
        )) {
            is UseCaseSyncResults.Results -> createSession(result.data.requestToken)
            is UseCaseSyncResults.Error -> notifyFailure(result.errMessage)
        }
    }

    private fun createSession(token: String) {
        when (val result = createSessionUseCaseSync.createSessionSync(
            token
        )) {
            is UseCaseSyncResults.Results -> fetchAccountDetail(result.data.sessionId)
            is UseCaseSyncResults.Error -> notifyFailure(result.errMessage)
        }
    }

    private fun fetchAccountDetail(sessionId: String) {
        when (val result = fetchAccountDetailsUseCaseSync.getAccountDetailsUseCaseSync(sessionId)) {
            is UseCaseSyncResults.Results -> {
                notifySuccess()
            }
            is UseCaseSyncResults.Error -> notifyFailure(result.errMessage)
        }
    }

    private fun notifySuccess() {
        uiThreadPoster.post {
            listeners.forEach {
                it.onLoggedIn()
            }
        }
        becomeNotBusy()
    }

    private fun notifyFailure(msg: String) {
        uiThreadPoster.post {
            listeners.forEach {
                it.onLoginFailed(msg)
            }
        }
        becomeNotBusy()
    }

}