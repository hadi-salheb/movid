package com.hadysalhab.movid.movies.usecases.wishlist

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

class FetchWatchlistMoviesUseCase(
    private val getSessionIdUseCaseSync: GetSessionIdUseCaseSync,
    private val getAccountDetailsUseCaseSync: GetAccountDetailsUseCaseSync,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val errorMessageHandler: ErrorMessageHandler,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val uiThreadPoster: UiThreadPoster,
    private val tmdbApi: TmdbApi
) : BaseBusyObservable<FetchWatchlistMoviesUseCase.Listener>() {
    interface Listener {
        fun onFetchWatchlistMoviesSuccess(movies: MoviesResponse)
        fun onFetchWatchlistMoviesFailure(msg: String)
    }

    fun fetchWishlistMoviesUseCase(page: Int) {
        assertNotBusyAndBecomeBusy()
        backgroundThreadPoster.post {
            val sessionId = getSessionIdUseCaseSync.getSessionIdUseCaseSync()
            val accountId = getAccountDetailsUseCaseSync.getAccountDetailsUseCaseSync().id
            when (val response = fireRequest(page, accountId, sessionId)) {
                is ApiSuccessResponse -> {
                    val moviesResponse =
                        schemaToModelHelper.getMoviesResponseFromSchema(
                            GroupType.WATCHLIST,
                            response.body
                        )
                    notifySuccess(moviesResponse)
                }
                is ApiEmptyResponse, is ApiErrorResponse -> {
                    notifyFailure(errorMessageHandler.getErrorMessageFromApiResponse(response))
                }
            }
        }
    }

    private fun fireRequest(
        page: Int,
        accountId: Int,
        sessionId: String
    ): ApiResponse<MoviesResponseSchema> = try {
        val res = tmdbApi.getWatchlistMovies(
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
            listeners.forEach { it.onFetchWatchlistMoviesSuccess(movies) }
            becomeNotBusy()
        }
    }

    private fun notifyFailure(err: String) {
        uiThreadPoster.post {
            listeners.forEach { it.onFetchWatchlistMoviesFailure(err) }
            becomeNotBusy()
        }
    }

}