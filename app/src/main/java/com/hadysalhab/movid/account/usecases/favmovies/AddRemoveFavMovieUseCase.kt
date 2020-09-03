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
    private val moviesStateManager: MoviesStateManager,
    private val getSessionIdUseCaseSync: GetSessionIdUseCaseSync,
    private val getAccountDetailsUseCaseSync: GetAccountDetailsUseCaseSync,
    private val errorMessageHandler: ErrorMessageHandler,
    private val tmdbApi: TmdbApi
) : BaseBusyObservable<AddRemoveFavMovieUseCase.Listener>() {
    interface Listener {
        fun onAddRemoveFavorites()
        fun onAddRemoveFavoritesSuccess(movieDetail: MovieDetail)
        fun onAddRemoveFavoritesFailure(err: String)
    }

    var movieId: Int? = null

    fun addRemoveFavUseCase(movieId: Int, favorite: Boolean) {
        assertNotBusyAndBecomeBusy()
        this.movieId = movieId
        backgroundThreadPoster.post {
            notifyLoading()
            val accountResponse = getAccountDetailsUseCaseSync.getAccountDetailsUseCaseSync()
            val res = addRemoveFav(
                accountID = accountResponse.id,
                mediaID = movieId,
                sessionId = getSessionIdUseCaseSync.getSessionIdUseCaseSync(),
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
                val currentMovieInStore = moviesStateManager.getMovieDetailById(movieId!!)
                    ?: throw RuntimeException("Movie cannot be null in the store while trying to add/remove it to/from fav!!")
                val movie = currentMovieInStore.copy(
                    accountStates = currentMovieInStore.accountStates.copy(favorite = !currentMovieInStore.accountStates.favorite)
                ).also {
                    it.timeStamp = currentMovieInStore.timeStamp
                }
                moviesStateManager.upsertMovieDetailToList(movie)
                notifySuccess(movie)
            }
            is ApiEmptyResponse -> {
                notifyFailure(errorMessageHandler.createErrorMessage(""))

            }
            is ApiErrorResponse -> {
                notifyFailure(errorMessageHandler.createErrorMessage(res.errorMessage))
            }
        }
    }

    private fun notifyLoading() {
        uiThreadPoster.post {
            listeners.forEach {
                it.onAddRemoveFavorites()
            }
        }
    }

    private fun notifySuccess(newMovie: MovieDetail) {
        becomeNotBusy()
        uiThreadPoster.post {
            listeners.forEach {
                it.onAddRemoveFavoritesSuccess(newMovie)
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