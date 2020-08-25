package com.hadysalhab.movid.movies.usecases.list

import android.util.Log
import com.google.gson.Gson
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
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
    private val dataValidator: DataValidator,
    private val timeProvider: TimeProvider
) : BaseBusyObservable<FetchMovieListUseCase.Listener>() {
    interface Listener {
        fun onFetchingMovieList()
        fun onFetchMovieListSuccess(movies: List<Movie>)
        fun onFetchMovieListFailed(msg: String)
    }

    private lateinit var errorMessage: String
    private var page = 1
    lateinit var moviesResponse: MoviesResponse

    fun fetchMovieListAndNotify(region: String, groupType: String, movieID: Int?,pageToFetch:Int) {
        if (this::moviesResponse.isInitialized && pageToFetch > moviesResponse.total_pages) {
            return
        }
        this.page = pageToFetch
        assertNotBusyAndBecomeBusy()
        when (groupType) {
            GroupType.POPULAR.value -> {
                getPopularMovies(region)
            }
            GroupType.NOW_PLAYING.value -> {
                getNowPlayingMovies(region)
            }
            GroupType.UPCOMING.value -> {
                getUpcomingMovies(region)
            }
            GroupType.TOP_RATED.value -> {
                getTopRatedMovies(region)
            }
            GroupType.SIMILAR_MOVIES.value -> {
                fetchSimilarMovies(movieID)
            }
            GroupType.RECOMMENDED_MOVIES.value -> {
                fetchRecommendedMovies(movieID)
            }
            else -> throw RuntimeException("GroupType not supported")
        }

    }

    //Caching for similar movies list is not supported
    private fun fetchSimilarMovies(movieID: Int?) {
        listeners.forEach {
            it.onFetchingMovieList()
        }
        backgroundThreadPoster.post {
            val res = fetchSimilarMoviesUseCaseSync.fetchSimilarMoviesUseCaseSync(
                movieID!!,
                page

            ) //movieId !=null should be handled in the fragment initialization
            handleResponse(res, GroupType.SIMILAR_MOVIES)
        }
    }

    private fun fetchRecommendedMovies(movieID: Int?) {
        listeners.forEach {
            it.onFetchingMovieList()
        }
        backgroundThreadPoster.post {
            val res = fetchRecommendedMoviesUseCaseSync.fetchRecommendedMoviesUseCaseSync(
                movieID!!, page
            ) //movieId !=null should be handled in the fragment initialization
            handleResponse(res, GroupType.RECOMMENDED_MOVIES)
        }
    }

    private fun getPopularMovies(region: String) {
        //check if 24hrs passed when requesting the first page
        if (page == 1) {
            if (dataValidator.isMoviesResponseValid(moviesStateManager.popularMovies)) {
                moviesResponse = moviesStateManager.popularMovies.deepCopy()
                notifySuccess()
                return
            }
        }
        fetchPopularMovies(region)
    }

    private fun fetchPopularMovies(region: String) {
        listeners.forEach {
            it.onFetchingMovieList()
        }
        backgroundThreadPoster.post {
            val res = fetchPopularMoviesUseCaseSync.fetchPopularMoviesSync(region, page)
            handleResponse(res, GroupType.POPULAR)
        }

    }

    private fun getNowPlayingMovies(region: String) {
        //check if 24hrs passed when requesting the first page
        if (page == 1) {
            if (dataValidator.isMoviesResponseValid(moviesStateManager.nowPlayingMovies)) {
                moviesResponse = moviesStateManager.nowPlayingMovies.deepCopy()
                notifySuccess()
                return
            }
        }
        fetNowPlayingMovies(region)
    }

    private fun fetNowPlayingMovies(region: String) {
        listeners.forEach {
            it.onFetchingMovieList()
        }
        backgroundThreadPoster.post {
            val res = fetchNowPlayingMoviesUseCaseSync.fetchNowPlayingMoviesSync(region, page)
            handleResponse(res, GroupType.POPULAR)
        }

    }

    private fun getTopRatedMovies(region: String) {
        //check if 24hrs passed when requesting the first page
        if (page == 1) {
            if (dataValidator.isMoviesResponseValid(moviesStateManager.topRatedMovies)) {
                moviesResponse = moviesStateManager.topRatedMovies.deepCopy()
                notifySuccess()
                return
            }
        }
        fetchTopRatedMovies(region)
    }

    private fun fetchTopRatedMovies(region: String) {
        listeners.forEach {
            it.onFetchingMovieList()
        }
        backgroundThreadPoster.post {
            val res = fetchTopRatedMoviesUseCaseSync.fetchTopRatedMoviesSync(region, page)
            handleResponse(res, GroupType.POPULAR)
        }

    }

    private fun getUpcomingMovies(region: String) {
        //check if 24hrs passed when requesting the first page
        if (page == 1) {
            if (dataValidator.isMoviesResponseValid(moviesStateManager.upcomingMovies)) {
                moviesResponse = moviesStateManager.upcomingMovies.deepCopy()
                notifySuccess()
                return
            }
        }
        fetchUpcomingMovies(region)
    }

    private fun fetchUpcomingMovies(region: String) {
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
                if (page == 1) {
                    this.moviesResponse = movieGroup
                } else {
                    this.moviesResponse.movies!!.addAll(movieGroup.movies ?: emptyList())
                }
                notifySuccess()
            }
            is ApiEmptyResponse -> {
                createErrorMessage("")
                notifyFailure()
            }
            is ApiErrorResponse -> {
                createErrorMessage(response.errorMessage)
                notifyFailure()
            }
        }

    }

    private fun getMovieResponse(groupType: GroupType, moviesResponseSchema: MoviesResponseSchema) =
        when (groupType) {
            GroupType.POPULAR -> {
                createMoviesResponse(moviesResponseSchema, GroupType.POPULAR).also {
                    if (page == 1) {
                        moviesStateManager.updatePopularMovies(it)
                    }
                }
            }
            GroupType.UPCOMING -> {
                createMoviesResponse(moviesResponseSchema, GroupType.UPCOMING).also {
                    if (page == 1) {
                        moviesStateManager.updateUpcomingMovies(it)
                    }
                }
            }
            GroupType.TOP_RATED -> {
                createMoviesResponse(moviesResponseSchema, GroupType.TOP_RATED).also {
                    if (page == 1) {
                        moviesStateManager.updateTopRatedMovies(it)
                    }
                }


            }
            GroupType.NOW_PLAYING -> {
                createMoviesResponse(moviesResponseSchema, GroupType.NOW_PLAYING).also {
                    if (page == 1) {
                        moviesStateManager.updateNowPlayingMovies(it)
                    }
                }
            }
            GroupType.SIMILAR_MOVIES -> {
                createMoviesResponse(moviesResponseSchema, GroupType.SIMILAR_MOVIES)
            }
            GroupType.RECOMMENDED_MOVIES -> {
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
        ).apply {
            if(page==1) {
                timeStamp = timeProvider.currentTimestamp
            }
        }

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
            becomeNotBusy()
        }
    }

    // notify controller
    private fun notifySuccess() {
        uiThreadPoster.post {
            listeners.forEach {
                it.onFetchMovieListSuccess(moviesResponse.movies!!)
            }
            becomeNotBusy()
        }
    }
}
