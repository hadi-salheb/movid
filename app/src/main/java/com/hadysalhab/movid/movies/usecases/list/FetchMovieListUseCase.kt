package com.hadysalhab.movid.movies.usecases.list

import com.google.gson.Gson
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.usecases.popular.FetchPopularMoviesUseCaseSync
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.networking.responses.MovieSchema
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import com.hadysalhab.movid.networking.responses.TmdbErrorResponse
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchMovieListUseCase(
    private val fetchPopularMoviesUseCaseSync: FetchPopularMoviesUseCaseSync,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val moviesStateManager: MoviesStateManager,
    private val gson: Gson
) : BaseBusyObservable<FetchMovieListUseCase.Listener>() {
    interface Listener {
        fun onFetchingMovieList(page:Int)
        fun onFetchMovieListSuccess(moviesResponse: MoviesResponse)
        fun onFetchMovieListFailed(msg: String)
    }

    private lateinit var moviesResponse: MoviesResponse
    private lateinit var errorMessage: String

    fun fetchMovieListAndNotify(region: String, groupType: GroupType, page: Int) {
        when (groupType) {
            GroupType.POPULAR -> {
                getPopularMovies(region, page)
            }
            else -> throw RuntimeException("GroupType not supported")
        }

    }

    private fun getPopularMovies(region: String, pageInRequest: Int) {
        //check if 24hrs passed when requesting the first page
        if (pageInRequest == 1) {
            if (moviesStateManager.arePopularMoviesValid) {
                this.moviesResponse = moviesStateManager.popularMovies
                notifySuccess()
            } else {
                fetchPopularMovies(region, 1)
            }
            // avoid checking expiration during pagination
        } else {
            val currentAvailablePage = moviesStateManager.popularMovies.page
            //process death happened
            if (currentAvailablePage == 0) {
                fetchPopularMovies(region, 1)
            } else {
                when (pageInRequest) {
                    in 1..currentAvailablePage -> {
                        this.moviesResponse = moviesStateManager.popularMovies
                        notifySuccess()
                    }
                    else -> {
                        if (currentAvailablePage == moviesStateManager.popularMovies.total_pages) {
                            return
                        }
                        fetchPopularMovies(region, currentAvailablePage + 1)
                    }
                }
            }
        }
    }

    private fun fetchPopularMovies(region: String, page: Int) {
        listeners.forEach {
            it.onFetchingMovieList(page)
        }
        backgroundThreadPoster.post {
            val res = fetchPopularMoviesUseCaseSync.fetchPopularMoviesSync(region, page)
            handleResponse(res, GroupType.POPULAR)
        }

    }

    private fun handleResponse(response: ApiResponse<MoviesResponseSchema>, tag: GroupType) {
        when (response) {
            is ApiSuccessResponse -> {
                val movieGroup = getMovieResponse(tag, response.body)
                this.moviesResponse = movieGroup
                notifySuccess()
            }
            is ApiEmptyResponse -> {
                val movieGroup = getMovieResponse(
                    tag, MoviesResponseSchema(
                        1, 0, 1,
                        emptyList()
                    )
                )
                this.moviesResponse = movieGroup
                notifySuccess()
            }
            is ApiErrorResponse -> {
                createErrorMessage(response.errorMessage)
            }
        }

    }

    private fun getMovieResponse(groupType: GroupType, moviesResponseSchema: MoviesResponseSchema) =
        when (groupType) {
            GroupType.POPULAR -> {
                val popular = createMoviesResponse(moviesResponseSchema, GroupType.POPULAR)
                moviesStateManager.setPopularMovies(popular)
                moviesStateManager.popularMovies
            }
            GroupType.UPCOMING -> {
                val upcoming = createMoviesResponse(moviesResponseSchema, GroupType.UPCOMING)
                moviesStateManager.setUpcomingMovies(upcoming)
                moviesStateManager.upcomingMovies
            }
            GroupType.TOP_RATED -> {
                val topRated = createMoviesResponse(moviesResponseSchema, GroupType.TOP_RATED)
                moviesStateManager.setTopRatedMovies(topRated)
                moviesStateManager.topRatedMovies
            }
            GroupType.NOW_PLAYING -> {
                val nowPlaying = createMoviesResponse(moviesResponseSchema, GroupType.NOW_PLAYING)
                moviesStateManager.setNowPlayingMovies(nowPlaying)
                moviesStateManager.nowPlayingMovies
            }
            else -> throw RuntimeException("GroupType $groupType not supported in this UseCase")
        }

    private fun createMoviesResponse(moviesResponse: MoviesResponseSchema, groupType: GroupType) =
        MoviesResponse(
            moviesResponse.page,
            moviesResponse.totalResults,
            moviesResponse.total_pages,
            getMovies(moviesResponse.movies),
            groupType
        )

    private fun getMovies(moviesSchema: List<MovieSchema>): MutableList<Movie> {
        val movies = mutableListOf<Movie>()
        movies.addAll(moviesSchema.map { movieSchema ->
            with(movieSchema) {
                Movie(
                    id,
                    title,
                    posterPath,
                    backdropPath,
                    voteAvg,
                    voteCount,
                    releaseDate,
                    overview
                )
            }
        })
        return movies
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

    // notify controller
    private fun notifyFailure() {
        uiThreadPoster.post {
            listeners.forEach {
                it.onFetchMovieListFailed(errorMessage)
            }
        }
        becomeNotBusy()
    }

    // notify controller
    private fun notifySuccess() {
        uiThreadPoster.post {
            listeners.forEach {
                it.onFetchMovieListSuccess(this.moviesResponse)
            }
        }
        becomeNotBusy()
    }
}
