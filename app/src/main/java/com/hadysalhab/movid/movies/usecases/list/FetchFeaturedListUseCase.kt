package com.hadysalhab.movid.movies.usecases.list

import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.usecases.factory.BaseFeaturedMoviesUseCaseFactory
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchFeaturedListUseCase(
    private val baseFeaturedMoviesUseCaseFactory: BaseFeaturedMoviesUseCaseFactory,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val errorMessageHandler: ErrorMessageHandler,
    private val moviesStateManager: MoviesStateManager,
    private val timeProvider: TimeProvider
) :
    BaseBusyObservable<FetchFeaturedListUseCase.Listener>() {
    interface Listener {
        fun onFetchMoviesResponseSuccess(movies: MoviesResponse)
        fun onFetchMoviesResponseFailure(msg: String)
    }

    private var page = 1
    private lateinit var region: String
    private lateinit var groupType: GroupType
    private val LOCK = Object()

    fun fetchMoviesResponseUseCase(
        groupType: GroupType,
        page: Int,
        region: String
    ) {
        assertNotBusyAndBecomeBusy()
        synchronized(LOCK) {
            this.page = page
            this.groupType = groupType
            this.region = region
        }

        fetchFeaturedMovies()
    }

    private fun fetchFeaturedMovies() {
        val useCase =
            baseFeaturedMoviesUseCaseFactory.createBaseFeaturedMoviesUseCase(
                groupType
            )
        backgroundThreadPoster.post {
            val result = useCase.fetchFeaturedMoviesUseCase(
                page,
                this.region
            )
            handleResult(groupType, result)
        }
    }

    private fun handleResult(groupType: GroupType, response: ApiResponse<MoviesResponseSchema>) {
        when (response) {
            is ApiSuccessResponse -> {
                val result = schemaToModelHelper.getMoviesResponseFromSchema(
                    groupType,
                    response.body
                )
                result.timeStamp = timeProvider.currentTimestamp
                result.region = this.region
                moviesStateManager.updateMoviesResponse(result)
                notifySuccess(
                    result
                )
            }
            is ApiErrorResponse, is ApiEmptyResponse -> {
                notifyFailure(errorMessageHandler.getErrorMessageFromApiResponse(response))
            }
        }
    }

    // notify controller
    private fun notifyFailure(error: String) {
        uiThreadPoster.post {
            listeners.forEach {
                it.onFetchMoviesResponseFailure(error)
            }
            becomeNotBusy()
        }
    }

    // notify controller
    private fun notifySuccess(movies: MoviesResponse) {
        uiThreadPoster.post {
            listeners.forEach {
                it.onFetchMoviesResponseSuccess(movies)
            }
            becomeNotBusy()
        }
    }
}
