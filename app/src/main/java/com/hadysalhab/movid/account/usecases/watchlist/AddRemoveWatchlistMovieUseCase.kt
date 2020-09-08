package com.hadysalhab.movid.account.usecases.watchlist

import com.hadysalhab.movid.account.usecases.details.GetAccountDetailsUseCaseSync
import com.hadysalhab.movid.account.usecases.session.GetSessionIdUseCaseSync
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.WatchlistFavoriteResponse
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster


class AddRemoveWatchlistMovieUseCase(
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val getSessionIdUseCaseSync: GetSessionIdUseCaseSync,
    private val getAccountDetailsUseCaseSync: GetAccountDetailsUseCaseSync,
    private val errorMessageHandler: ErrorMessageHandler,
    private val tmdbApi: TmdbApi,
    private val moviesStateManager: MoviesStateManager
) : BaseBusyObservable<AddRemoveWatchlistMovieUseCase.Listener>() {
    interface Listener {
        fun onAddRemoveWatchlistSuccess(movieDetail: MovieDetail)
        fun onAddRemoveWatchlistFailure(err: String)
    }

    private var movieID: Int? = null
    fun addRemoveWatchlistUseCase(movieId: Int, watchlist: Boolean) {
        this.movieID = movieId
        assertNotBusyAndBecomeBusy()
        backgroundThreadPoster.post {
            val accountResponse = getAccountDetailsUseCaseSync.getAccountDetailsUseCaseSync()
            val sessionId = getSessionIdUseCaseSync.getSessionIdUseCaseSync()
            val res = addRemoveWatchlist(
                accountID = accountResponse.id,
                mediaID = movieId,
                sessionId = sessionId,
                watchlist = watchlist
            )
            handleResponse(res)
        }
    }

    private fun addRemoveWatchlist(
        accountID: Int,
        mediaID: Int,
        sessionId: String,
        watchlist: Boolean
    ): ApiResponse<WatchlistFavoriteResponse> = try {
        val httpBodyRequest = WatchlistHttpBodyRequest(mediaId = mediaID, watchlist = watchlist)
        val res = tmdbApi.addToWatchlist(
            accountID = accountID,
            sessionID = sessionId,
            httpBodyRequest = httpBodyRequest
        ).execute()
        ApiResponse.create<WatchlistFavoriteResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }

    private fun handleResponse(res: ApiResponse<WatchlistFavoriteResponse>) {
        when (res) {
            is ApiSuccessResponse -> {
                val oldMovieDetail = moviesStateManager.getMovieDetailById(movieId = this.movieID!!)
                if (oldMovieDetail == null) {
                    throw  RuntimeException("$oldMovieDetail should be part of the store when changing watchlist state")
                }
                val newMovieDetail =
                    oldMovieDetail.copy(accountStates = oldMovieDetail.accountStates.copy(watchlist = !oldMovieDetail.accountStates.watchlist))
                newMovieDetail.timeStamp = oldMovieDetail.timeStamp
                moviesStateManager.upsertMovieDetailToList(newMovieDetail)
                notifySuccess(newMovieDetail)
            }
            is ApiEmptyResponse, is ApiErrorResponse -> {
                notifyFailure(errorMessageHandler.getErrorMessageFromApiResponse(res))
            }
        }
    }

    private fun notifySuccess(movieDetail: MovieDetail) {
        becomeNotBusy()
        uiThreadPoster.post {
            listeners.forEach {
                it.onAddRemoveWatchlistSuccess(movieDetail)
            }
        }
    }

    private fun notifyFailure(err: String) {
        becomeNotBusy()
        uiThreadPoster.post {
            listeners.forEach {
                it.onAddRemoveWatchlistFailure(err)
            }
        }
    }
}