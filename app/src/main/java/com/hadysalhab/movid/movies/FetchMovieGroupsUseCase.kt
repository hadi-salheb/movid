package com.hadysalhab.movid.movies

import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.networking.responses.MovieSchema
import com.hadysalhab.movid.networking.responses.MoviesResponse
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

/**
 * UseCase that fetch popular,top-rated,upcoming movies
 * Notifies the client for success (if all calls succeeded), for failure (if only one calls fails)
 * */

class FetchMovieGroupsUseCase(
    private val fetchPopularMoviesUseCase: FetchPopularMoviesUseCase,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster
) :
    BaseBusyObservable<FetchMovieGroupsUseCase.Listener>() {
    interface Listener {
        fun onFetchMovieGroupsSucceeded(movieGroups: List<MovieGroup>)
        fun onFetchMovieGroupsFailed(msg: String)
    }

    private var mNumbOfFinishedUseCase = 0
    private var isAnyUseCaseFailed = false
    private val LOCK = Object()
    private val movieGroups = mutableListOf<MovieGroup>()
    private lateinit var errorMessage: String

    fun fetchMovieGroupsAndNotify() {
        backgroundThreadPoster.post {
            backgroundThreadPoster.post {
                fetchPopularMovies()
            }
            waitForAllUseCasesToFinish()
            synchronized(LOCK) {
                if (isAnyUseCaseFailed) {
                    notifyFailure()
                } else {
                    notifySuccess()
                }
            }
        }
    }

    private fun fetchPopularMovies() {
        val res = fetchPopularMoviesUseCase.fetchPopularMoviesSync()
        handleResponse(res, MovieGroupType.POPULAR)
    }

    private fun waitForAllUseCasesToFinish() {
        synchronized(LOCK) {
            while (mNumbOfFinishedUseCase < 1 && !isAnyUseCaseFailed) {
                try {
                    LOCK.wait()
                } catch (e: InterruptedException) {
                    return
                }
            }
        }
    }

    private fun handleResponse(
        response: ApiResponse<MoviesResponse>,
        movieGroupType: MovieGroupType
    ) {
        synchronized(LOCK) {
            when (response) {
                is ApiSuccessResponse -> {
                    val movieGroup = MovieGroup(movieGroupType, getMovies(response.body.movies))
                    movieGroups.add(movieGroup)
                }
                is ApiEmptyResponse -> {
                    val movieGroup = MovieGroup(movieGroupType, emptyList())
                    movieGroups.add(movieGroup)
                }
                is ApiErrorResponse -> {
                    isAnyUseCaseFailed = true
                    if (!this::errorMessage.isInitialized) {
                        errorMessage = response.errorMessage
                    }
                }
            }
            mNumbOfFinishedUseCase++
            LOCK.notifyAll()
        }
    }

    private fun getMovies(moviesSchema: List<MovieSchema>) = moviesSchema.map { movieSchema ->
        with(movieSchema) {
            Movie(id, title, posterPath, voteAverage, voteCount, releaseDate)
        }
    }

    private fun notifyFailure() {
        uiThreadPoster.post {
            listeners.forEach {
                it.onFetchMovieGroupsFailed(errorMessage)
            }
        }
    }

    private fun notifySuccess() {
        uiThreadPoster.post {
            listeners.forEach {
                it.onFetchMovieGroupsSucceeded(movieGroups)
            }
        }
    }
}
