package com.hadysalhab.movid.movies.usecases.list

import com.google.gson.Gson
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.usecases.nowplaying.FetchNowPlayingMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.popular.FetchPopularMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.recommended.FetchRecommendedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.similar.FetchSimilarMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.toprated.FetchTopRatedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.upcoming.FetchUpcomingMoviesUseCaseSync
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
    private val fetchUpcomingMoviesUseCaseSync: FetchUpcomingMoviesUseCaseSync,
    private val fetchTopRatedMoviesUseCaseSync: FetchTopRatedMoviesUseCaseSync,
    private val fetchNowPlayingMoviesUseCaseSync: FetchNowPlayingMoviesUseCaseSync,
    private val fetchSimilarMoviesUseCaseSync: FetchSimilarMoviesUseCaseSync,
    private val fetchRecommendedMoviesUseCaseSync: FetchRecommendedMoviesUseCaseSync,
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

    fun fetchMovieListAndNotify(region: String, groupType: String, page: Int, movieID: Int?) {
        assertNotBusyAndBecomeBusy()
        this.page = page
        when (groupType) {
            GroupType.POPULAR.value -> {
                getPopularMovies(region, page)
            }
            GroupType.NOW_PLAYING.value -> {
                getNowPlayingMovies(region, page)
            }
            GroupType.UPCOMING.value -> {
                getUpcomingMovies(region, page)
            }
            GroupType.TOP_RATED.value -> {
                getTopRatedMovies(region, page)
            }
            GroupType.SIMILAR_MOVIES.value -> {
                fetchSimilarMovies(movieID, page)
            }
            GroupType.RECOMMENDED_MOVIES.value->{
                fetchRecommendedMovies(movieID,page)
            }
            else -> throw RuntimeException("GroupType not supported")
        }

    }

    //Caching for similar movies list is not supported
    private fun fetchSimilarMovies(movieID: Int?, pageInRequest: Int) {
        listeners.forEach {
            it.onFetchingMovieList()
        }
        backgroundThreadPoster.post {
            val res = fetchSimilarMoviesUseCaseSync.fetchSimilarMoviesUseCaseSync(
                movieID!!,
                pageInRequest

            ) //movieId !=null should be handled in the fragment initialization
            handleResponse(res, GroupType.SIMILAR_MOVIES)
        }
    }
    private fun fetchRecommendedMovies(movieID: Int?, pageInRequest: Int) {
        listeners.forEach {
            it.onFetchingMovieList()
        }
        backgroundThreadPoster.post {
            val res = fetchRecommendedMoviesUseCaseSync.fetchRecommendedMoviesUseCaseSync(
                movieID!!,
                pageInRequest

            ) //movieId !=null should be handled in the fragment initialization
            handleResponse(res, GroupType.RECOMMENDED_MOVIES)
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

    private fun getNowPlayingMovies(region: String, pageInRequest: Int) {
        //check if 24hrs passed when requesting the first page
        if (pageInRequest == 1) {
            if (dataValidator.isMoviesResponseValid(moviesStateManager.nowPlayingMovies)) {
                notifySuccess(moviesStateManager.nowPlayingMovies)
                return
            }
        }
        fetNowPlayingMovies(region, pageInRequest)
    }

    private fun fetNowPlayingMovies(region: String, page: Int) {
        listeners.forEach {
            it.onFetchingMovieList()
        }
        backgroundThreadPoster.post {
            val res = fetchNowPlayingMoviesUseCaseSync.fetchNowPlayingMoviesSync(region, page)
            handleResponse(res, GroupType.POPULAR)
        }

    }

    private fun getTopRatedMovies(region: String, pageInRequest: Int) {
        //check if 24hrs passed when requesting the first page
        if (pageInRequest == 1) {
            if (dataValidator.isMoviesResponseValid(moviesStateManager.topRatedMovies)) {
                notifySuccess(moviesStateManager.topRatedMovies)
                return
            }
        }
        fetchTopRatedMovies(region, pageInRequest)
    }

    private fun fetchTopRatedMovies(region: String, page: Int) {
        listeners.forEach {
            it.onFetchingMovieList()
        }
        backgroundThreadPoster.post {
            val res = fetchTopRatedMoviesUseCaseSync.fetchTopRatedMoviesSync(region, page)
            handleResponse(res, GroupType.POPULAR)
        }

    }

    private fun getUpcomingMovies(region: String, pageInRequest: Int) {
        //check if 24hrs passed when requesting the first page
        if (pageInRequest == 1) {
            if (dataValidator.isMoviesResponseValid(moviesStateManager.upcomingMovies)) {
                notifySuccess(moviesStateManager.upcomingMovies)
                return
            }
        }
        fetchUpcomingMovies(region, pageInRequest)
    }

    private fun fetchUpcomingMovies(region: String, page: Int) {
        listeners.forEach {
            it.onFetchingMovieList()
        }
        backgroundThreadPoster.post {
            val res = fetchUpcomingMoviesUseCaseSync.fetchUpcomingMoviesSync(region, page)
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
            GroupType.SIMILAR_MOVIES -> {
                createMoviesResponse(moviesResponseSchema, GroupType.SIMILAR_MOVIES)
            }
            GroupType.RECOMMENDED_MOVIES->{
                createMoviesResponse(moviesResponseSchema, GroupType.RECOMMENDED_MOVIES)
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
