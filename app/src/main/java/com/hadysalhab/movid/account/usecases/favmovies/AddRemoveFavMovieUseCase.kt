package com.hadysalhab.movid.account.usecases.favmovies

import com.hadysalhab.movid.account.usecases.details.GetAccountDetailsUseCaseSync
import com.hadysalhab.movid.account.usecases.session.GetSessionIdUseCaseSync
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.utils.BaseBusyObservable
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
    private val tmdbApi: TmdbApi
) : BaseBusyObservable<AddRemoveFavMovieUseCase.Listener>() {
    interface Listener {
        fun onAddRemoveFavoritesSuccess()
        fun onAddRemoveFavoritesFailure(err: String)
    }
    fun addRemoveFavUseCase(movieId: Int, favorite: Boolean) {
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
                notifySuccess()
            }
            is ApiEmptyResponse, is ApiErrorResponse -> {
                notifyFailure(errorMessageHandler.getErrorMessageFromApiResponse(res))
            }
        }
    }

    private fun notifySuccess() {
        becomeNotBusy()
        uiThreadPoster.post {
            listeners.forEach {
                it.onAddRemoveFavoritesSuccess()
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