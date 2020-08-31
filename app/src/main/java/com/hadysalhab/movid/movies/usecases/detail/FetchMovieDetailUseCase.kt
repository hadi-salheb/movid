package com.hadysalhab.movid.movies.usecases.detail

import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.ErrorMessageHandler
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MovieDetailSchema
import retrofit2.Call
import retrofit2.Response

/**
 * UseCase that fetch popular,top-rated,upcoming movies
 * Notifies the client for success (if all calls succeeded), for failure (if only one calls fails)
 * */

class FetchMovieDetailUseCase(
    private val tmdbApi: TmdbApi,
    private val moviesStateManager: MoviesStateManager,
    private val timeProvider: TimeProvider,
    private val dataValidator: DataValidator,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val errorMessageHandler: ErrorMessageHandler
) :
    BaseBusyObservable<FetchMovieDetailUseCase.Listener>() {
    interface Listener {
        fun onFetchMovieDetailSuccess(movieDetail: MovieDetail)
        fun onFetchMovieDetailFailed(msg: String)
        fun onFetchingMovieDetail()
    }

    private lateinit var errorMessage: String

    fun fetchMovieDetailAndNotify(movieId: Int, sessionId: String) {
        val movieDetail = moviesStateManager.getMovieDetailById(movieId)
        if (dataValidator.isMovieDetailValid(movieDetail)) {
            notifySuccess(movieDetail!!)
        } else {
            listeners.forEach {
                it.onFetchingMovieDetail()
            }
            // will throw an exception if a client triggered this flow while it is busy
            assertNotBusyAndBecomeBusy()
            tmdbApi.fetchMovieDetail(
                movieId = movieId,
                sessionID = sessionId
            ).enqueue(object : retrofit2.Callback<MovieDetailSchema> {
                override fun onFailure(call: Call<MovieDetailSchema>, t: Throwable) {
                    errorMessageHandler.createErrorMessage(t.message ?: "Unable to resolve host")
                    notifyFailure(errorMessage)
                }

                override fun onResponse(
                    call: Call<MovieDetailSchema>,
                    schema: Response<MovieDetailSchema>
                ) {
                    if (schema.body() == null || schema.code() == 204) {
                        errorMessageHandler.createErrorMessage("")
                    }
                    val movieDetailResult = schemaToModelHelper.getMovieDetails(schema).apply {
                        timeStamp = timeProvider.currentTimestamp
                    }
                    moviesStateManager.addMovieDetailToList(movieDetailResult)
                    notifySuccess(movieDetailResult)
                }
            })
        }
    }

    private fun notifyFailure(msg: String) {
        // notify controller
        listeners.forEach {
            it.onFetchMovieDetailFailed(msg)
        }
        becomeNotBusy()
    }

    private fun notifySuccess(movieDetail: MovieDetail) {
        // notify controller
        listeners.forEach {
            it.onFetchMovieDetailSuccess(movieDetail)
        }
        becomeNotBusy()
    }
}
