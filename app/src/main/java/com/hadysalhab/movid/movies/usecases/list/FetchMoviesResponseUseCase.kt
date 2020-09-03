package com.hadysalhab.movid.movies.usecases.list

import com.hadysalhab.movid.common.usecases.UseCaseSyncResults
import com.hadysalhab.movid.common.usecases.factory.BaseFeaturedMoviesUseCaseFactory
import com.hadysalhab.movid.common.usecases.factory.BaseSimilarRecommendedMoviesUseCaseFactory
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchMoviesResponseUseCase(
    private val baseSimilarRecommendedMoviesUseCaseFactory: BaseSimilarRecommendedMoviesUseCaseFactory,
    private val baseFeaturedMoviesUseCaseFactory: BaseFeaturedMoviesUseCaseFactory,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster
) :
    BaseBusyObservable<FetchMoviesResponseUseCase.Listener>() {
    interface Listener {
        fun onFetchMoviesResponseSuccess(movies: MoviesResponse)
        fun onFetchMoviesResponseFailure(msg: String)
        fun onFetchMoviesResponse()
    }

    private lateinit var errorMessage: String
    private var movieId: Int? = null
    private var page = 1
    private val LOCK = Object()

    fun fetchMoviesResponseUseCase(groupType: GroupType, page: Int, movieId: Int?) {
        assertNotBusyAndBecomeBusy()
        listeners.forEach {
            it.onFetchMoviesResponse()
        }
        if ((groupType == GroupType.SIMILAR_MOVIES || groupType == GroupType.RECOMMENDED_MOVIES) && movieId == null) {
            throw RuntimeException("Cannot fetch similar movies or recommended movies with null movie id")
        }
        synchronized(LOCK) {
            errorMessage = ""
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
                handleResult(result)
            } else {
                val useCase =
                    baseFeaturedMoviesUseCaseFactory.createBaseFeaturedMoviesUseCase(
                        groupType
                    )
                val result = useCase.fetchFeaturedMoviesUseCase(page)
                handleResult(result)
            }
        }
    }

    private fun handleResult(result: UseCaseSyncResults<MoviesResponse>) {
        when (result) {
            is UseCaseSyncResults.Results -> {
                notifySuccess(result.data)
            }
            is UseCaseSyncResults.Error -> {
                notifyFailure(result.errMessage)
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