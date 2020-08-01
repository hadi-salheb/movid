package com.hadysalhab.movid.movies

import com.google.gson.Gson
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MovieDetailResponse
import com.hadysalhab.movid.networking.responses.TmdbErrorResponse
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster
import retrofit2.Call
import retrofit2.Response

/**
 * UseCase that fetch popular,top-rated,upcoming movies
 * Notifies the client for success (if all calls succeeded), for failure (if only one calls fails)
 * */

class FetchMovieDetailUseCase(
    private val tmdbApi: TmdbApi,
    private val gson: Gson,
    private val moviesStateManager: MoviesStateManager,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster
) :
    BaseBusyObservable<FetchMovieDetailUseCase.Listener>() {
    interface Listener {
        fun onFetchMovieDetailSuccess()
        fun onFetchMovieDetailFailed(msg: String)
    }

    private lateinit var errorMessage: String

    fun fetchMovieDetailAndNotify() {
        // will throw an exception if a client triggered this flow while it is busy
        assertNotBusyAndBecomeBusy()
        backgroundThreadPoster.post {
            tmdbApi.fetchMovieDetail(
                id = 475557,
                sessionID = "2877a3e3269c78640398b0b47db8c82a93a3774d"
            ).enqueue(object : retrofit2.Callback<MovieDetailResponse> {
                override fun onFailure(call: Call<MovieDetailResponse>, t: Throwable) {
                    notifyFailure()
                }

                override fun onResponse(
                    call: Call<MovieDetailResponse>,
                    response: Response<MovieDetailResponse>
                ) {
                    notifySuccess()
                }

            })
        }
    }


    private fun createErrorMessage(errMessage: String) {
        this.errorMessage = when {
            errMessage.contains("status_message") -> {
                gson.fromJson(errMessage, TmdbErrorResponse::class.java).statusMessage
            }
            errMessage.contains("Unable to resolve host") -> {
                "Please check network connection and try again"
            }
            else -> {
                "Unable to retrieve data. Please try again.!"
            }
        }
    }

    private fun notifyFailure() {
        uiThreadPoster.post {
            listeners.forEach {
            }
        }
        becomeNotBusy()
    }

    private fun notifySuccess() {
        uiThreadPoster.post {
            listeners.forEach {
            }
        }
        becomeNotBusy()
    }
}
