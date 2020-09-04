package com.hadysalhab.movid.movies.usecases.list

import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.usecases.factory.BaseFeaturedMoviesUseCaseFactory
import com.hadysalhab.movid.common.usecases.factory.BaseSimilarRecommendedMoviesUseCaseFactory
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchMoviesResponseUseCase(
    private val baseSimilarRecommendedMoviesUseCaseFactory: BaseSimilarRecommendedMoviesUseCaseFactory,
    private val baseFeaturedMoviesUseCaseFactory: BaseFeaturedMoviesUseCaseFactory,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val deviceConfigManager: DeviceConfigManager,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val errorMessageHandler: ErrorMessageHandler
) :
    BaseBusyObservable<FetchMoviesResponseUseCase.Listener>() {
    interface Listener {
        fun onFetchMoviesResponseSuccess(movies: MoviesResponse)
        fun onFetchMoviesResponseFailure(msg: String)
    }

    private var movieId: Int? = null
    private var page = 1
    private val LOCK = Object()

    fun fetchMoviesResponseUseCase(groupType: GroupType, page: Int, movieId: Int?) {
        assertNotBusyAndBecomeBusy()
        if ((groupType == GroupType.SIMILAR_MOVIES || groupType == GroupType.RECOMMENDED_MOVIES) && movieId == null) {
            throw RuntimeException("Cannot fetch similar movies or recommended movies with null movie id")
        }
        synchronized(LOCK) {
            this.movieId = movieId
            this.page = page
        }
        backgroundThreadPoster.post {
            if (groupType == GroupType.SIMILAR_MOVIES || groupType == GroupType.RECOMMENDED_MOVIES) {
                val useCase =
                    baseSimilarRecommendedMoviesUseCaseFactory.createSimilarRecommendedMoviesUseCase(
                        groupType
                    )
                val result = useCase.fetchSimilarRecommendedMoviesUseCase(page, movieId!!)
                handleResult(groupType, result)
            } else {
                val useCase =
                    baseFeaturedMoviesUseCaseFactory.createBaseFeaturedMoviesUseCase(
                        groupType
                    )
                val result = useCase.fetchFeaturedMoviesUseCase(
                    page,
                    deviceConfigManager.getISO3166CountryCodeOrUS()
                )
                handleResult(groupType, result)
            }
        }
    }

    private fun handleResult(groupType: GroupType, response: ApiResponse<MoviesResponseSchema>) {
        when (response) {
            is ApiSuccessResponse -> {
                notifySuccess(
                    schemaToModelHelper.getMoviesResponseFromSchema(
                        groupType,
                        response.body
                    )
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
        }
        becomeNotBusy()
    }

    // notify controller
    private fun notifySuccess(movies: MoviesResponse) {
        uiThreadPoster.post {
            listeners.forEach {
                it.onFetchMoviesResponseSuccess(movies)
            }
        }
        becomeNotBusy()
    }
}