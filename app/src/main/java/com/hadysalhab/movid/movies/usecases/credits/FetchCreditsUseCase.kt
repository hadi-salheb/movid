package com.hadysalhab.movid.movies.usecases.credits

import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.Credits
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.CreditsSchema
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchCreditsUseCase(
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val tmdbApi: TmdbApi,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val errorMessageHandler: ErrorMessageHandler
) : BaseBusyObservable<FetchCreditsUseCase.Listener>() {

    interface Listener {
        fun fetchCreditsSuccess(credits: Credits)
        fun fetchCreditsFailure(errorMessage: String)
    }

    fun fetchCreditsUseCase(movieID: Int) {
        assertNotBusyAndBecomeBusy()
        backgroundThreadPoster.post {
            val response = fetchCredits(movieID)
            handleResponse(response)
        }
    }

    private fun fetchCredits(movieId: Int) = try {
        val response = tmdbApi.getCredits(
            movieId = movieId
        ).execute()
        ApiResponse.create(response)
    } catch (err: Throwable) {
        ApiResponse.create<CreditsSchema>(err)
    }

    private fun handleResponse(response: ApiResponse<CreditsSchema>) {
        when (response) {
            is ApiSuccessResponse -> {
                val credits = schemaToModelHelper.getCredits(response.body)
                notifySuccess(credits)
            }
            is ApiEmptyResponse, is ApiErrorResponse -> {
                notifyFailure(errorMessageHandler.getErrorMessageFromApiResponse(response))
            }
        }
    }

    private fun notifySuccess(credits: Credits) {
        uiThreadPoster.post {
            listeners.forEach {
                it.fetchCreditsSuccess(credits)
            }
            becomeNotBusy()
        }
    }

    private fun notifyFailure(err: String) {
        uiThreadPoster.post {
            listeners.forEach {
                it.fetchCreditsFailure(err)
            }
            becomeNotBusy()
        }
    }

}