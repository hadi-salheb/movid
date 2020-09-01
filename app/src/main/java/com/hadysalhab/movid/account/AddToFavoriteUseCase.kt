package com.hadysalhab.movid.account

import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.ErrorMessageHandler
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.networking.responses.AddToFavResponse
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class AddToFavoriteUseCase(
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val moviesStateManager: MoviesStateManager,
    private val getSessionIdUseCaseSync: GetSessionIdUseCaseSync,
    private val getAccountDetailsUseCaseSync: GetAccountDetailsUseCaseSync,
    private val addToFavoriteUseCaseSync: AddToFavoriteUseCaseSync,
    private val errorMessageHandler: ErrorMessageHandler
) : BaseBusyObservable<AddToFavoriteUseCase.Listener>() {
    interface Listener {
        fun onAddToFavorites()
        fun onAddToFavoritesSuccess(movieDetail: MovieDetail)
        fun onAddToFavoritesFailure(err: String)
    }

    var movieId: Int? = null

    fun addToFavoriteUseCase(movieId: Int) {
        assertNotBusyAndBecomeBusy()
        this.movieId = movieId
        backgroundThreadPoster.post {
            notifyLoading()
            val accountResponse = getAccountDetailsUseCaseSync.getAccountDetailsUseCaseSync()
            val res = addToFavoriteUseCaseSync.addToFavoriteUseCaseSync(
                accountID = accountResponse.id,
                mediaID = movieId,
                sessionId = getSessionIdUseCaseSync.getSessionIdUseCaseSync()
            )
            handleResponse(res)
        }
    }

    private fun handleResponse(res: ApiResponse<AddToFavResponse>) {
        when (res) {
            is ApiSuccessResponse -> {
                val currentMovieInStore = moviesStateManager.getMovieDetailById(movieId!!)
                    ?: throw RuntimeException("Movie cannot be null in the store while trying to add it to fav!!")
                val movie = currentMovieInStore.copy(
                    accountStates = currentMovieInStore.accountStates.copy(favorite = true)
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
                it.onAddToFavorites()
            }
        }
    }

    private fun notifySuccess(newMovie: MovieDetail) {
        becomeNotBusy()
        uiThreadPoster.post {
            listeners.forEach {
                it.onAddToFavoritesSuccess(newMovie)
            }
        }
    }

    private fun notifyFailure(err: String) {
        becomeNotBusy()
        uiThreadPoster.post {
            listeners.forEach {
                it.onAddToFavoritesFailure(err)
            }
        }
    }
}