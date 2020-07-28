package com.hadysalhab.movid.movies

import com.google.gson.Gson
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.networking.responses.MovieSchema
import com.hadysalhab.movid.networking.responses.MoviesResponse
import com.hadysalhab.movid.networking.responses.TmdbErrorResponse
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

/**
 * UseCase that fetch popular,top-rated,upcoming movies
 * Notifies the client for success (if all calls succeeded), for failure (if only one calls fails)
 * */

class FetchMovieGroupsUseCase(
    private val fetchPopularMoviesUseCase: FetchPopularMoviesUseCase,
    private val fetchTopRatedMoviesUseCase: FetchTopRatedMoviesUseCase,
    private val fetchUpcomingMoviesUseCase: FetchUpcomingMoviesUseCase,
    private val fetchNowPlayingMoviesUseCase: FetchNowPlayingMoviesUseCase,
    private val fetchLatestMoviesUseCase: FetchLatestMoviesUseCase,
    private val gson: Gson,
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
    private val computations: Array<() -> Unit> = arrayOf(
        ::fetchPopularMovies, ::fetchTopRatedMovies,
        ::fetchUpcomingMovies,  ::fetchNowPlayingMovies
    )

    fun fetchMovieGroupsAndNotify() {
        backgroundThreadPoster.post {
            computations.forEach {
                backgroundThreadPoster.post {
                    it.invoke()
                }
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

    private fun fetchTopRatedMovies() {
        val res = fetchTopRatedMoviesUseCase.fetchTopRatedMoviesSync()
        handleResponse(res, MovieGroupType.TOP_RATED)
    }

    private fun fetchUpcomingMovies() {
        val res = fetchUpcomingMoviesUseCase.fetchUpcomingMoviesSync()
        handleResponse(res, MovieGroupType.UPCOMING)
    }

    private fun fetchLatestMovies() {
        val res = fetchLatestMoviesUseCase.fetchLatestMoviesSync()
        handleResponse(res, MovieGroupType.LATEST)
    }

    private fun fetchNowPlayingMovies() {
        val res = fetchNowPlayingMoviesUseCase.fetchNowPlayingMoviesSync()
        handleResponse(res, MovieGroupType.NOW_PLAYING)
    }

    private fun waitForAllUseCasesToFinish() {
        synchronized(LOCK) {
            while (mNumbOfFinishedUseCase < computations.size && !isAnyUseCaseFailed) {
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
                    createErrorMessage(response.errorMessage)
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
