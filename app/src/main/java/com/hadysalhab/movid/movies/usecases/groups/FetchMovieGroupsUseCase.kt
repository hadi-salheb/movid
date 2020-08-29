package com.hadysalhab.movid.movies.usecases.groups


import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.*
import com.hadysalhab.movid.movies.usecases.latest.FetchLatestMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.nowplaying.FetchNowPlayingMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.popular.FetchPopularMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.toprated.FetchTopRatedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.upcoming.FetchUpcomingMoviesUseCaseSync
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
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
    private val moviesStateManager: MoviesStateManager,
    private val timeProvider: TimeProvider,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val errorMessageHandler: ErrorMessageHandler
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
        val popularMoviesStore: MoviesResponse = moviesStateManager.getPopularMovies()
        val upcomingMoviesStore: MoviesResponse = moviesStateManager.getUpcomingMovies()
        val nowPLayingMoviesStore: MoviesResponse = moviesStateManager.getNowPlayingMovies()
        val topRatedMoviesStore: MoviesResponse = moviesStateManager.getTopRatedMovies()
        if (dataValidator.isMoviesResponseValid(popularMoviesStore)) this.movieGroups.add(
            popularMoviesStore
        ) else computations.add(
            this::fetchPopularMovies
        )
        if (dataValidator.isMoviesResponseValid(upcomingMoviesStore)) this.movieGroups.add(
            upcomingMoviesStore
        ) else computations.add(
            this::fetchUpcomingMovies
        )
        if (dataValidator.isMoviesResponseValid(nowPLayingMoviesStore)) this.movieGroups.add(
            nowPLayingMoviesStore
        ) else computations.add(
            this::fetchNowPlayingMovies
        )
        if (dataValidator.isMoviesResponseValid(topRatedMoviesStore)) this.movieGroups.add(
            topRatedMoviesStore
        ) else computations.add(
            this::fetchTopRatedMovies
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
                    isAnyUseCaseFailed = true
                    this.errorMessage = errorMessageHandler.createErrorMessage("")
                }
                is ApiErrorResponse -> {
                    isAnyUseCaseFailed = true
                    this.errorMessage =
                        errorMessageHandler.createErrorMessage(responseSchemaSchema.errorMessage)
                }
            }
            mNumbOfFinishedUseCase++
            LOCK.notifyAll()
        }
    }

    private fun getMovieResponse(groupType: GroupType, moviesResponseSchema: MoviesResponseSchema) =
        schemaToModelHelper.getMoviesResponseFromSchema(
            groupType,
            moviesResponseSchema
        ).also {
            it.timeStamp = timeProvider.currentTimestamp
            moviesStateManager.updateMoviesResponseByGroupType(it, groupType)
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
