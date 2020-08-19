package com.hadysalhab.movid.movies.usecases.list

import com.google.gson.Gson
import com.hadysalhab.movid.common.datavalidator.DataValidator
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
    private val gson: Gson,
    private val dataValidator: DataValidator
) : BaseBusyObservable<FetchMovieListUseCase.Listener>() {
    interface Listener {
        fun onFetchingMovieList()
        fun onFetchMovieListSuccess(moviesResponse: MoviesResponse)
        fun onFetchMovieListFailed(msg: String)
    }

    private lateinit var errorMessage: String
    private var page = 1

    fun fetchMovieListAndNotify(region: String, groupType: String, page: Int) {
        assertNotBusyAndBecomeBusy()
        this.page = page
        when (groupType) {
            GroupType.POPULAR.value -> {
                getPopularMovies(region, page)
            }
            else -> throw RuntimeException("GroupType not supported")
        }

    }

    private fun getPopularMovies(region: String, pageInRequest: Int) {
        //check if 24hrs passed when requesting the first page
        if (pageInRequest == 1) {
            if (dataValidator.isMoviesResponseValid(moviesStateManager.popularMovies)) {
                notifySuccess(moviesStateManager.popularMovies)
                return
            }
        }
        fetchPopularMovies(region, pageInRequest)
    }

    private fun fetchPopularMovies(region: String, page: Int) {
        listeners.forEach {
            it.onFetchingMovieList()
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
                notifySuccess(movieGroup)
            }
            is ApiEmptyResponse -> {
                val movieGroup = getMovieResponse(
                    tag, MoviesResponseSchema(
                        1, 0, 1,
                        emptyList()
                    )
                )
                notifySuccess(movieGroup)
            }
            is ApiErrorResponse -> {
                createErrorMessage(response.errorMessage)
            }
        }

    }

    private fun getMovieResponse(groupType: GroupType, moviesResponseSchema: MoviesResponseSchema) =
        when (groupType) {
            GroupType.POPULAR -> {
                var popular = createMoviesResponse(moviesResponseSchema, GroupType.POPULAR)
                if (page == 1) {
                    moviesStateManager.updatePopularMovies(popular)
                    popular = moviesStateManager.popularMovies
                }
                popular
            }
            GroupType.UPCOMING -> {
                var upcoming = createMoviesResponse(moviesResponseSchema, GroupType.UPCOMING)
                if (page == 1) {
                    moviesStateManager.updateUpcomingMovies(upcoming)
                    upcoming = moviesStateManager.upcomingMovies
                }
                upcoming

            }
            GroupType.TOP_RATED -> {
                var topRated = createMoviesResponse(moviesResponseSchema, GroupType.TOP_RATED)
                if (page == 1) {
                    moviesStateManager.updateTopRatedMovies(topRated)
                    topRated = moviesStateManager.topRatedMovies
                }
                topRated
            }
            GroupType.NOW_PLAYING -> {
                var nowPlaying = createMoviesResponse(moviesResponseSchema, GroupType.NOW_PLAYING)
                if (page == 1) {
                    moviesStateManager.updateNowPlayingMovies(nowPlaying)
                    nowPlaying = moviesStateManager.nowPlayingMovies
                }
                nowPlaying
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
    private fun notifySuccess(moviesResponse: MoviesResponse) {
        uiThreadPoster.post {
            listeners.forEach {
                it.onFetchMovieListSuccess(moviesResponse)
            }
        }
        becomeNotBusy()
    }
}
