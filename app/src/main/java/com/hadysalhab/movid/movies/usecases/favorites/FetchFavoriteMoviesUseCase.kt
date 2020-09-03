package com.hadysalhab.movid.movies.usecases.favorites

import com.hadysalhab.movid.account.usecases.details.GetAccountDetailsUseCaseSync
import com.hadysalhab.movid.account.usecases.session.GetSessionIdUseCaseSync
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchFavoriteMoviesUseCase(
    private val getSessionIdUseCaseSync: GetSessionIdUseCaseSync,
    private val getAccountDetailsUseCaseSync: GetAccountDetailsUseCaseSync,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val errorMessageHandler: ErrorMessageHandler,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val uiThreadPoster: UiThreadPoster,
    private val tmdbApi: TmdbApi
) : BaseBusyObservable<FetchFavoriteMoviesUseCase.Listener>() {
    interface Listener {
        fun onFetchFavoriteMoviesSuccess(movies: MoviesResponse)
        fun onFetchFavoriteMoviesFailure(msg: String)
        fun onFetchFavoriteMovies()
    }

    fun fetchFavoriteMoviesUseCase(page: Int) {
        assertNotBusyAndBecomeBusy()
        listeners.forEach {
            it.onFetchFavoriteMovies()
        }
        backgroundThreadPoster.post {
            when (val response = fireRequest(page)) {
                is ApiSuccessResponse -> {
                    val moviesResponse =
                        schemaToModelHelper.getMoviesResponseFromSchema(
                            GroupType.FAVORITES,
                            response.body
                        )
                    notifySuccess(moviesResponse)
                }
                is ApiEmptyResponse -> {
                    notifyFailure(errorMessageHandler.createErrorMessage(""))
                }
                is ApiErrorResponse -> {
                    notifyFailure(errorMessageHandler.createErrorMessage(response.errorMessage))
                }
            }
        }
    }

    private fun fireRequest(page: Int): ApiResponse<MoviesResponseSchema> = try {
        val sessionId = getSessionIdUseCaseSync.getSessionIdUseCaseSync()
        val accountId = getAccountDetailsUseCaseSync.getAccountDetailsUseCaseSync().id
        val res = tmdbApi.getFavoriteMovies(
            accountID = accountId,
            page = page,
            sessionID = sessionId
        ).execute()
        ApiResponse.create<MoviesResponseSchema>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }

    private fun notifySuccess(movies: MoviesResponse) {
        uiThreadPoster.post {
            listeners.forEach { it.onFetchFavoriteMoviesSuccess(movies) }
            becomeNotBusy()
        }
    }

    private fun notifyFailure(err: String) {
        uiThreadPoster.post {
            listeners.forEach { it.onFetchFavoriteMoviesFailure(err) }
            becomeNotBusy()
        }
    }

}