package com.hadysalhab.movid.movies.usecases.detail

import com.hadysalhab.movid.account.usecases.session.GetSessionIdUseCaseSync
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.MovieDetailSchema
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

/**
 * UseCase that fetch popular,top-rated,upcoming movies
 * Notifies the client for success (if all calls succeeded), for failure (if only one calls fails)
 * */

class FetchMovieDetailUseCase(
    private val tmdbApi: TmdbApi,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val errorMessageHandler: ErrorMessageHandler,
    private val getSessionIdUseCaseSync: GetSessionIdUseCaseSync,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster
) :
    BaseBusyObservable<FetchMovieDetailUseCase.Listener>() {
    interface Listener {
        fun onFetchMovieDetailSuccess(movieDetail: MovieDetail)
        fun onFetchMovieDetailFailed(msg: String)
    }

    private val sessionId = getSessionIdUseCaseSync.getSessionIdUseCaseSync()

    fun fetchMovieDetailAndNotify(movieId: Int) {
        assertNotBusyAndBecomeBusy()
        backgroundThreadPoster.post {
            val response = fetchApi(movieId)
            handleResponse(response)
        }
    }

    private fun handleResponse(response: ApiResponse<MovieDetailSchema>) {
        when (response) {
            is ApiSuccessResponse -> {
                notifySuccess(schemaToModelHelper.getMovieDetails(response.body))
            }
            is ApiEmptyResponse, is ApiErrorResponse -> {
                notifyFailure(errorMessageHandler.getErrorMessageFromApiResponse(response))
            }
        }
    }

    private fun fetchApi(movieId: Int) = try {
        val response = tmdbApi.fetchMovieDetail(
            movieId = movieId,
            sessionID = sessionId
        ).execute()
        ApiResponse.create(response)
    } catch (err: Throwable) {
        ApiResponse.create<MovieDetailSchema>(err)
    }

    private fun notifyFailure(msg: String) {
        uiThreadPoster.post {
            // notify controller
            listeners.forEach {
                it.onFetchMovieDetailFailed(msg)
            }
            becomeNotBusy()
        }
    }

    private fun notifySuccess(movieDetail: MovieDetail) {
        uiThreadPoster.post {
            // notify controller
            listeners.forEach {
                it.onFetchMovieDetailSuccess(movieDetail)
            }
            becomeNotBusy()
        }
    }
}
