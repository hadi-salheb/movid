package com.hadysalhab.movid.authentication

import com.hadysalhab.movid.account.usecases.details.FetchAccountDetailsUseCaseSync
import com.hadysalhab.movid.account.usecases.details.UpdateAccountDetailsUseCaseSync
import com.hadysalhab.movid.authentication.createsession.CreateSessionUseCaseSync
import com.hadysalhab.movid.authentication.createtoken.CreateRequestTokenUseCaseSync
import com.hadysalhab.movid.authentication.signtoken.SignTokenUseCaseSync
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster


class LoginUseCase(
    private val createRequestTokenUseCaseSync: CreateRequestTokenUseCaseSync,
    private val signTokenUseCaseSync: SignTokenUseCaseSync,
    private val createSessionUseCaseSync: CreateSessionUseCaseSync,
    private val fetchAccountDetailsUseCaseSync: FetchAccountDetailsUseCaseSync,
    private val updateAccountDetailsUseCaseSync: UpdateAccountDetailsUseCaseSync,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val errorMessageHandler: ErrorMessageHandler
) :
    BaseBusyObservable<LoginUseCase.Listener>() {
    interface Listener {
        fun onLoggedIn()
        fun onLoginFailed(msg: String)
    }

    private var userName: String = ""
    private var password: String = ""


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
        when (val response = createRequestTokenUseCaseSync.createRequestTokenSync()) {
            is ApiSuccessResponse -> signTokenWithLogin(response.body.requestToken)
            is ApiErrorResponse, is ApiEmptyResponse -> notifyFailure(
                errorMessageHandler.getErrorMessageFromApiResponse(
                    response
                )
            )
        }
    }

    private fun signTokenWithLogin(token: String) {
        when (val response = signTokenUseCaseSync.signTokenSync(
            userName,
            password,
            token
        )) {
            is ApiSuccessResponse -> createSession(response.body.requestToken)
            is ApiErrorResponse, is ApiEmptyResponse -> notifyFailure(
                errorMessageHandler.getErrorMessageFromApiResponse(
                    response
                )
            )
        }
    }

    private fun createSession(token: String) {
        when (val response = createSessionUseCaseSync.createSessionSync(
            token
        )) {
            is ApiSuccessResponse -> fetchAccountDetail(response.body.sessionId)
            is ApiErrorResponse, is ApiEmptyResponse -> notifyFailure(
                errorMessageHandler.getErrorMessageFromApiResponse(
                    response
                )
            )
        }
    }

    private fun fetchAccountDetail(sessionId: String) {
        when (val response =
            fetchAccountDetailsUseCaseSync.getAccountDetailsUseCaseSync(sessionId)) {
            is ApiSuccessResponse -> {
                updateAccountDetailsUseCaseSync.updateUserStateUseCaseSync(
                    sessionId,
                    schemaToModelHelper.getAccountResponse(response.body)
                )
                notifySuccess()
            }
            is ApiErrorResponse, is ApiEmptyResponse -> notifyFailure(
                errorMessageHandler.getErrorMessageFromApiResponse(
                    response
                )
            )
        }
    }

    private fun notifySuccess() {
        uiThreadPoster.post {
            listeners.forEach {
                it.onLoggedIn()
            }
            becomeNotBusy()
        }
    }

    private fun notifyFailure(msg: String) {
        uiThreadPoster.post {
            listeners.forEach {
                it.onLoginFailed(msg)
            }
            becomeNotBusy()
        }
    }

}