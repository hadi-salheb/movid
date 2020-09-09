package com.hadysalhab.movid.movies.usecases.detail

import com.hadysalhab.movid.account.usecases.session.GetSessionIdUseCaseSync
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.CollectionSchema
import com.hadysalhab.movid.networking.responses.MovieDetailSchema
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

/**
 * UseCase that fetch popular,top-rated,upcoming movies
 * Notifies the client for success (if all calls succeeded), for failure (if only one calls fails)
 * */

class FetchMovieDetailUseCase(
    private val tmdbApi: TmdbApi,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val errorMessageHandler: ErrorMessageHandler,
    private val getSessionIdUseCaseSync: GetSessionIdUseCaseSync,
    private val timeProvider: TimeProvider,
    private val dataValidator: DataValidator,
    private val moviesStateManager: MoviesStateManager,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster
) :
    BaseBusyObservable<FetchMovieDetailUseCase.Listener>() {
    interface Listener {
        fun onFetchMovieDetailSuccess(movieDetail: MovieDetail)
        fun onFetchMovieDetailFailed(msg: String)
    }

    private val sessionId = getSessionIdUseCaseSync.getSessionIdUseCaseSync()

    fun fetchMovieDetailAndNotify(movieId: Int) {
        assertNotBusyAndBecomeBusy()
        val store = moviesStateManager.getMovieDetailById(movieId)
        if (dataValidator.isMovieDetailValid(store)) {
            notifySuccess(store!!)
        } else {
            backgroundThreadPoster.post {
                val response = fetchMovieDetail(movieId)
                handleResponse(response)
            }
        }
    }

    private fun handleResponse(response: ApiResponse<MovieDetailSchema>) {
        when (response) {
            is ApiSuccessResponse -> {
                if (response.body.belongToCollection == null) {
                    val movieDetail = schemaToModelHelper.getMovieDetails(response.body)
                    movieDetail.timeStamp = timeProvider.currentTimestamp
                    moviesStateManager.upsertMovieDetailToList(movieDetail)
                    notifySuccess(movieDetail)
                } else {
                    val collectionResponse = fetchCollection(response.body.belongToCollection.id)
                    handleCollectionResponse(collectionResponse, response.body)
                }
            }
            is ApiEmptyResponse, is ApiErrorResponse -> {
                notifyFailure(errorMessageHandler.getErrorMessageFromApiResponse(response))
            }
        }
    }

    private fun handleCollectionResponse(
        response: ApiResponse<CollectionSchema>,
        movieDetailSchema: MovieDetailSchema
    ) {
        when (response) {
            is ApiSuccessResponse -> {
                val movieDetail =
                    schemaToModelHelper.getMovieDetails(movieDetailSchema, response.body)
                movieDetail.timeStamp = timeProvider.currentTimestamp
                moviesStateManager.upsertMovieDetailToList(movieDetail)
                notifySuccess(movieDetail)
            }
            is ApiEmptyResponse, is ApiErrorResponse -> {
                notifyFailure(errorMessageHandler.getErrorMessageFromApiResponse(response))
            }
        }
    }

    private fun fetchMovieDetail(movieId: Int) = try {
        val response = tmdbApi.fetchMovieDetail(
            movieId = movieId,
            sessionID = sessionId
        ).execute()
        ApiResponse.create(response)
    } catch (err: Throwable) {
        ApiResponse.create<MovieDetailSchema>(err)
    }

    private fun fetchCollection(collectionID: Int) = try {
        val response = tmdbApi.getCollection(collectionID).execute()
        ApiResponse.create(response)
    } catch (err: Throwable) {
        ApiResponse.create<CollectionSchema>(err)
    }

    private fun notifyFailure(msg: String) {
        uiThreadPoster.post {
            // notify controller
            listeners.forEach {
                it.onFetchMovieDetailFailed(msg)
            }
            becomeNotBusy()
        }
    }

    private fun notifySuccess(movieDetail: MovieDetail) {
        uiThreadPoster.post {
            // notify controller
            listeners.forEach {
                it.onFetchMovieDetailSuccess(movieDetail)
            }
            becomeNotBusy()
        }
    }
}
