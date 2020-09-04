package com.hadysalhab.movid.account.usecases.favmovies

import com.hadysalhab.movid.account.usecases.details.GetAccountDetailsUseCaseSync
import com.hadysalhab.movid.account.usecases.session.GetSessionIdUseCaseSync
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.AddToFavResponse
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster


class AddRemoveFavMovieUseCase(
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val getSessionIdUseCaseSync: GetSessionIdUseCaseSync,
    private val getAccountDetailsUseCaseSync: GetAccountDetailsUseCaseSync,
    private val errorMessageHandler: ErrorMessageHandler,
    private val tmdbApi: TmdbApi,
    private val moviesStateManager: MoviesStateManager
) : BaseBusyObservable<AddRemoveFavMovieUseCase.Listener>() {
    interface Listener {
        fun onAddRemoveFavoritesSuccess(movieDetail: MovieDetail)
        fun onAddRemoveFavoritesFailure(err: String)
    }

    private var movieID: Int? = null
    fun addRemoveFavUseCase(movieId: Int, favorite: Boolean) {
        this.movieID = movieId
        assertNotBusyAndBecomeBusy()
        backgroundThreadPoster.post {
            val accountResponse = getAccountDetailsUseCaseSync.getAccountDetailsUseCaseSync()
            val sessionId = getSessionIdUseCaseSync.getSessionIdUseCaseSync()
            val res = addRemoveFav(
                accountID = accountResponse.id,
                mediaID = movieId,
                sessionId = sessionId,
                favorite = favorite
            )
            handleResponse(res)
        }
    }

    private fun addRemoveFav(
        accountID: Int,
        mediaID: Int,
        sessionId: String,
        favorite: Boolean
    ): ApiResponse<AddToFavResponse> = try {
        val httpBodyRequest = FavoriteHttpBodyRequest(mediaId = mediaID, favorite = favorite)
        val res = tmdbApi.markAsFavorite(
            accountID = accountID,
            sessionID = sessionId,
            httpBodyRequest = httpBodyRequest
        ).execute()
        ApiResponse.create<AddToFavResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }

    private fun handleResponse(res: ApiResponse<AddToFavResponse>) {
        when (res) {
            is ApiSuccessResponse -> {
                val oldMovieDetail = moviesStateManager.getMovieDetailById(movieId = this.movieID!!)
                if (oldMovieDetail == null) {
                    throw  RuntimeException("$oldMovieDetail should be part of the store when changing fav state")
                }
                val newMovieDetail =
                    oldMovieDetail.copy(accountStates = oldMovieDetail.accountStates.copy(favorite = !oldMovieDetail.accountStates.favorite))
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
                it.onAddRemoveFavoritesSuccess(movieDetail)
            }
        }
    }

    private fun notifyFailure(err: String) {
        becomeNotBusy()
        uiThreadPoster.post {
            listeners.forEach {
                it.onAddRemoveFavoritesFailure(err)
            }
        }
    }
}