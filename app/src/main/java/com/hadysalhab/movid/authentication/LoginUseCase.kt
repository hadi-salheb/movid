package com.hadysalhab.movid.authentication

import com.google.gson.Gson
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.networking.responses.TmdbErrorResponse
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

/**
 * UseCase that handles all the network calls required to login the user
 * Notifies the client for success (if all calls succeeded), for failure (if only one calls fails)
 * */

class LoginUseCase(
    private val createRequestTokenUseCase: CreateRequestTokenUseCase,
    private val signTokenUseCase: SignTokenUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val gson: Gson
) :
    BaseBusyObservable<LoginUseCase.Listener>() {
    interface Listener {
        fun onLoggedIn(sessionId: String)
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
        when (val res = createRequestTokenUseCase.createRequestTokenSync()) {
            is ApiSuccessResponse -> signTokenWithLogin(res.body.requestToken)
            is ApiEmptyResponse -> notifyFailure("Server Error")
            is ApiErrorResponse -> createErrMessageAndNotify(res.errorMessage)
        }
    }

    private fun signTokenWithLogin(token: String) {
        when (val res = signTokenUseCase.signTokenSync(
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
        when (val res = createSessionUseCase.createSessionSync(
            token
        )) {
            is ApiSuccessResponse -> notifySuccess(res.body.sessionId)
            is ApiEmptyResponse -> notifyFailure("Server Error")
            is ApiErrorResponse -> createErrMessageAndNotify(res.errorMessage)
        }
    }

    private fun notifySuccess(sessionId: String) {
        sharedPreferencesManager.setStoredSessionId(sessionId)
        uiThreadPoster.post {
            listeners.forEach {
                it.onLoggedIn(sessionId)
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