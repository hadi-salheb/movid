package com.hadysalhab.movid.movies.usecases.groups


import com.google.gson.Gson
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.usecases.latest.FetchLatestMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.nowplaying.FetchNowPlayingMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.popular.FetchPopularMoviesUseCaseSync
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

/**
 * UseCase that fetch popular,top-rated,upcoming movies
 * Notifies the client for success (if all calls succeeded), for failure (if only one calls fails)
 * */

class FetchMovieGroupsUseCase(
    private val fetchPopularMoviesUseCaseSync: FetchPopularMoviesUseCaseSync,
    private val fetchTopRatedMoviesUseCaseSync: FetchTopRatedMoviesUseCaseSync,
    private val fetchUpcomingMoviesUseCaseSync: FetchUpcomingMoviesUseCaseSync,
    private val fetchNowPlayingMoviesUseCaseSync: FetchNowPlayingMoviesUseCaseSync,
    private val fetchLatestMoviesUseCaseSync: FetchLatestMoviesUseCaseSync,
    private val dataValidator: DataValidator,
    private val gson: Gson,
    private val moviesStateManager: MoviesStateManager,
    private val timeProvider: TimeProvider,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster
) :
    BaseBusyObservable<FetchMovieGroupsUseCase.Listener>() {
    interface Listener {
        fun onFetchMovieGroupsSucceeded(movieGroups: List<MoviesResponse>)
        fun onFetchMovieGroupsFailed(msg: String)
        fun onFetching()
    }

    private var mNumbOfFinishedUseCase = 0
    private var isAnyUseCaseFailed = false
    private val LOCK = Object()
    private lateinit var movieGroups: MutableList<MoviesResponse>
    private lateinit var errorMessage: String
    private lateinit var region: String
    private lateinit var computations: MutableList<() -> Unit>

    fun fetchMovieGroupsAndNotify(region: String) {
        // will throw an exception if a client triggered this flow while it is busy
        assertNotBusyAndBecomeBusy()

        synchronized(LOCK) {
            movieGroups = mutableListOf()
            this.region = region
            mNumbOfFinishedUseCase = 0
            isAnyUseCaseFailed = false
            computations = mutableListOf()
            validateComputations()
            if (computations.size > 0) {
                // notify controller
                listeners.forEach {
                    it.onFetching()
                }
            }
        }
        backgroundThreadPoster.post {
            waitForAllUseCasesToFinish()
        }
        computations.forEach {
            backgroundThreadPoster.post {
                it.invoke()
            }
        }
    }

    private fun validateComputations() {
        if (dataValidator.isMoviesResponseValid(moviesStateManager.popularMovies)) this.movieGroups.add(
            moviesStateManager.popularMovies
        ) else computations.add(
            this::fetchPopularMovies
        )
        if (dataValidator.isMoviesResponseValid(moviesStateManager.upcomingMovies)) this.movieGroups.add(
            moviesStateManager.upcomingMovies
        ) else computations.add(
            this::fetchUpcomingMovies
        )
        if (dataValidator.isMoviesResponseValid(moviesStateManager.topRatedMovies)) this.movieGroups.add(
            moviesStateManager.topRatedMovies
        ) else computations.add(
            this::fetchTopRatedMovies
        )
        if (dataValidator.isMoviesResponseValid(moviesStateManager.nowPlayingMovies)) this.movieGroups.add(
            moviesStateManager.nowPlayingMovies
        ) else computations.add(
            this::fetchNowPlayingMovies
        )

    }

    private fun fetchPopularMovies() {
        val res = fetchPopularMoviesUseCaseSync.fetchPopularMoviesSync(region)
        handleResponse(res, GroupType.POPULAR)
    }

    private fun fetchTopRatedMovies() {
        val res = fetchTopRatedMoviesUseCaseSync.fetchTopRatedMoviesSync(region)
        handleResponse(res, GroupType.TOP_RATED)
    }

    private fun fetchUpcomingMovies() {
        val res = fetchUpcomingMoviesUseCaseSync.fetchUpcomingMoviesSync(region)
        handleResponse(res, GroupType.UPCOMING)
    }

    private fun fetchLatestMovies() {
        val res = fetchLatestMoviesUseCaseSync.fetchLatestMoviesSync()
        handleResponse(res, GroupType.LATEST)
    }

    private fun fetchNowPlayingMovies() {
        val res = fetchNowPlayingMoviesUseCaseSync.fetchNowPlayingMoviesSync(region)
        handleResponse(res, GroupType.NOW_PLAYING)
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
            if (isAnyUseCaseFailed) {
                notifyFailure()
            } else {
                notifySuccess()
            }
        }
    }

    private fun handleResponse(
        responseSchemaSchema: ApiResponse<MoviesResponseSchema>,
        groupType: GroupType
    ) {
        synchronized(LOCK) {
            when (responseSchemaSchema) {
                is ApiSuccessResponse -> {
                    val movieGroup = getMovieResponse(groupType, responseSchemaSchema.body)
                    movieGroups.add(movieGroup)
                }
                is ApiEmptyResponse -> {
                    val movieGroup = getMovieResponse(
                        groupType, MoviesResponseSchema(
                            1, 0, 1,
                            emptyList()
                        )
                    )
                    movieGroups.add(movieGroup)
                }
                is ApiErrorResponse -> {
                    isAnyUseCaseFailed = true
                    createErrorMessage(responseSchemaSchema.errorMessage)
                }
            }
            mNumbOfFinishedUseCase++
            LOCK.notifyAll()
        }
    }

    private fun getMovieResponse(groupType: GroupType, moviesResponseSchema: MoviesResponseSchema) =
        when (groupType) {
            GroupType.POPULAR -> {
                createMoviesResponse(moviesResponseSchema, GroupType.POPULAR).also {
                    moviesStateManager.updatePopularMovies(it)
                }
            }
            GroupType.UPCOMING -> {
                createMoviesResponse(moviesResponseSchema, GroupType.UPCOMING).also {
                    moviesStateManager.updateUpcomingMovies(it)
                }
            }
            GroupType.TOP_RATED -> {
                createMoviesResponse(moviesResponseSchema, GroupType.TOP_RATED).also {
                    moviesStateManager.updateTopRatedMovies(it)
                }
            }
            GroupType.NOW_PLAYING -> {
                createMoviesResponse(moviesResponseSchema, GroupType.NOW_PLAYING).also {
                    moviesStateManager.updateNowPlayingMovies(it)
                }
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
            timeStamp = timeProvider.currentTimestamp
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
                it.onFetchMovieGroupsFailed(errorMessage)
            }
        }
        becomeNotBusy()
    }

    // notify controller
    private fun notifySuccess() {
        uiThreadPoster.post {
            listeners.forEach {
                it.onFetchMovieGroupsSucceeded(movieGroups)
            }
        }
        becomeNotBusy()
    }
}
