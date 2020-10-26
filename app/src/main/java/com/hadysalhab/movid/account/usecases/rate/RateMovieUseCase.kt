package com.hadysalhab.movid.account.usecases.rate

import com.hadysalhab.movid.account.usecases.session.GetSessionIdUseCaseSync
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.AccountStateUpdateResponse
import com.hadysalhab.movid.screen.common.events.MovieDetailEvents
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster
import org.greenrobot.eventbus.EventBus


class RateMovieUseCase(
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val getSessionIdUseCaseSync: GetSessionIdUseCaseSync,
    private val errorMessageHandler: ErrorMessageHandler,
    private val tmdbApi: TmdbApi,
    private val moviesStateManager: MoviesStateManager
) : BaseBusyObservable<RateMovieUseCase.Listener>() {
    interface Listener {
        fun onRatingMovieFailure(err: String)
    }

    private var movieID: Int? = null
    private var rate: Double = 0.0

    fun rateMovieUseCase(movieId: Int, rate: Double) {

        this.movieID = movieId
        this.rate = rate

        assertNotBusyAndBecomeBusy()

        backgroundThreadPoster.post {
            val sessionId = getSessionIdUseCaseSync.getSessionIdUseCaseSync()
            val res = rateMovie(
                movieId = movieId,
                sessionId = sessionId,
                rate = rate
            )
            handleResponse(res)
        }
    }

    private fun rateMovie(
        movieId: Int,
        sessionId: String,
        rate: Double
    ): ApiResponse<AccountStateUpdateResponse> = try {
        val httpBodyRequest = RateMovieHttpBodyRequest(rate = rate)
        val res = tmdbApi.rateMovie(
            movieId = movieId,
            sessionID = sessionId,
            httpBodyRequest = httpBodyRequest
        ).execute()
        ApiResponse.create<AccountStateUpdateResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }

    private fun handleResponse(res: ApiResponse<AccountStateUpdateResponse>) {
        when (res) {
            is ApiSuccessResponse -> {
                val oldMovieDetail = moviesStateManager.getMovieDetailById(movieId = this.movieID!!)
                if (oldMovieDetail == null) {
                    throw  RuntimeException("$oldMovieDetail should be part of the store when changing rate state")
                }
                val newMovieDetail =
                    oldMovieDetail.copy(accountStates = oldMovieDetail.accountStates!!.copy(rated = this.rate))
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
        uiThreadPoster.post {
            EventBus.getDefault().post(MovieDetailEvents.RatingUpdate(movieDetail))
            becomeNotBusy()
        }
    }

    private fun notifyFailure(err: String) {
        uiThreadPoster.post {
            listeners.forEach {
                it.onRatingMovieFailure(err)
            }
            becomeNotBusy()
        }
    }
}