package com.hadysalhab.movid.authentication

import com.google.gson.Gson
import com.hadysalhab.movid.account.UserStateManager
import com.hadysalhab.movid.account.details.FetchAccountDetailsUseCaseSync
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.networking.responses.TmdbErrorResponse
import com.hadysalhab.movid.persistence.AccountDao
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster


class LoginUseCase(
    private val createRequestTokenUseCaseSync: CreateRequestTokenUseCaseSync,
    private val signTokenUseCaseSync: SignTokenUseCaseSync,
    private val createSessionUseCaseSync: CreateSessionUseCaseSync,
    private val fetchAccountDetailsUseCaseSync: FetchAccountDetailsUseCaseSync,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val gson: Gson,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val accountDao: AccountDao,
    private val userStateManager: UserStateManager
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
        when (val res = createRequestTokenUseCaseSync.createRequestTokenSync()) {
            is ApiSuccessResponse -> signTokenWithLogin(res.body.requestToken)
            is ApiEmptyResponse -> notifyFailure("Server Error")
            is ApiErrorResponse -> createErrMessageAndNotify(res.errorMessage)
        }
    }

    private fun signTokenWithLogin(token: String) {
        when (val res = signTokenUseCaseSync.signTokenSync(
            userName,
            password,
            token
        )) {
            is ApiSuccessResponse -> createSession(res.body.requestToken)
            is ApiEmptyResponse -> notifyFailure("Server Error")
            is ApiErrorResponse -> createErrMessageAndNotify(res.errorMessage)
        }
    }

    private fun createSession(token: String) {
        when (val res = createSessionUseCaseSync.createSessionSync(
            token
        )) {
            is ApiSuccessResponse -> fetchAccountDetail(res.body.sessionId)
            is ApiEmptyResponse -> notifyFailure("Server Error")
            is ApiErrorResponse -> createErrMessageAndNotify(res.errorMessage)
        }
    }

    private fun fetchAccountDetail(sessionId: String) {
        when (val res = fetchAccountDetailsUseCaseSync.getAccountDetailsUseCaseSync(sessionId)) {
            is ApiSuccessResponse -> {
                val accountResponse = schemaToModelHelper.getAccountResponse(res.body)
                sharedPreferencesManager.setStoredSessionId(sessionId)
                accountDao.insertAccountResponse(accountResponse)
                userStateManager.apply {
                    updateSessionId(sessionId)
                    updateAccountResponse(accountResponse)
                }
                notifySuccess()
            }
            is ApiEmptyResponse -> notifyFailure("Server Error")
            is ApiErrorResponse -> createErrMessageAndNotify(res.errorMessage)
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

    private fun createErrMessageAndNotify(errMessage: String) {
        val msg = when {
            errMessage.contains("status_message") -> {
                gson.fromJson(errMessage, TmdbErrorResponse::class.java).statusMessage
            }
            errMessage.contains("Unable to resolve host") -> {
                "Please check network connection"
            }
            else -> {
                errMessage
            }
        }
        notifyFailure(msg)
    }

}