package com.hadysalhab.movid.usecases

import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.persistence.SharedPreferencesManager
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class LoginUseCase(
    private val createRequestTokenUseCase: CreateRequestTokenUseCase,
    private val signTokenUseCase: SignTokenUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val sharedPreferencesManager: SharedPreferencesManager
) :
    BaseBusyObservable<LoginUseCase.Listener>() {
    interface Listener {
        fun onLoggedIn(sessionId: String)
        fun onLoginFailed(msg: String)
    }

    var userName: String = ""
    var password: String = ""


    fun loginAndNotify(username: String, password: String) {
        this.userName = username
        this.password = password
        backgroundThreadPoster.post {
            createRequestToken()
        }
    }

    private fun createRequestToken() {
        when (val res = createRequestTokenUseCase.createRequestTokenSync()) {
            is ApiSuccessResponse -> signTokenWithLogin(res.body.requestToken)
            is ApiEmptyResponse -> notifyFailure("Unknown Error\nCheck Network Connection")
            is ApiErrorResponse -> {
                if (res.errorMessage.contains("Unable to resolve host")) {
                    notifyFailure("Please check network connection")
                } else {
                    notifyFailure(res.errorMessage)
                }
            }
        }
    }

    private fun signTokenWithLogin(token: String) {
        when (val res = signTokenUseCase.signTokenSync(
            userName,
            password,
            token
        )) {
            is ApiSuccessResponse -> createSession(res.body.requestToken)
            is ApiEmptyResponse -> notifyFailure("Unknown Error\nCheck Network Connection")
            is ApiErrorResponse -> {
                if (res.errorMessage.contains("Unable to resolve host")) {
                    notifyFailure("Please check network connection")
                } else {
                    notifyFailure(res.errorMessage)
                }
            }
        }
    }

    private fun createSession(token: String) {
        when (val res = createSessionUseCase.createSessionSync(
            token
        )) {
            is ApiSuccessResponse -> notifySuccess(res.body.sessionId)
            is ApiEmptyResponse -> notifyFailure("Unknown Error\nCheck Network Connection")
            is ApiErrorResponse -> {
                if (res.errorMessage.contains("Unable to resolve host")) {
                    notifyFailure("Please check network connection")
                } else {
                    notifyFailure(res.errorMessage)
                }
            }
        }
    }

    private fun notifySuccess(sessionId: String) {
        sharedPreferencesManager.setStoredSessionId(sessionId)
        uiThreadPoster.post {
            listeners.forEach {
                it.onLoggedIn(sessionId)
            }
        }
    }

    private fun notifyFailure(msg: String) {
        uiThreadPoster.post {
            listeners.forEach {
                it.onLoginFailed(msg)
            }
        }

    }


}