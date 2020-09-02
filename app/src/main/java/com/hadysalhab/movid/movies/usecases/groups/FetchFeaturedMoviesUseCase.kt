package com.hadysalhab.movid.movies.usecases.groups


import com.hadysalhab.movid.common.usecases.UseCaseSyncResults
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.nowplaying.FetchNowPlayingMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.popular.FetchPopularMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.toprated.FetchTopRatedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.upcoming.FetchUpcomingMoviesUseCaseSync
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchFeaturedMoviesUseCase(
    private val fetchPopularMoviesUseCaseSync: FetchPopularMoviesUseCaseSync,
    private val fetchTopRatedMoviesUseCaseSync: FetchTopRatedMoviesUseCaseSync,
    private val fetchUpcomingMoviesUseCaseSync: FetchUpcomingMoviesUseCaseSync,
    private val fetchNowPlayingMoviesUseCaseSync: FetchNowPlayingMoviesUseCaseSync,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster
) :
    BaseBusyObservable<FetchFeaturedMoviesUseCase.Listener>() {
    interface Listener {
        fun onFetchMovieGroupsSucceeded(movieGroups: List<MoviesResponse>)
        fun onFetchMovieGroupsFailed(msg: String)
        fun onFetchMovieGroups()
    }

    private var mNumbOfFinishedUseCase = 0
    private var isAnyUseCaseFailed = false
    private val LOCK = Object()
    private lateinit var movieGroups: List<MoviesResponse>
    private lateinit var errorMessage: String
    private val computations: List<() -> Unit> =
        listOf(
            this::fetchTopRatedMovies,
            this::fetchNowPlayingMovies,
            this::fetchUpcomingMovies,
            this::fetchPopularMovies
        )

    fun fetchFeaturedMoviesUseCase() {
        // will throw an exception if a client triggered this flow while it is busy
        assertNotBusyAndBecomeBusy()
        listeners.forEach {
            it.onFetchMovieGroups()
        }

        synchronized(LOCK) {
            movieGroups = mutableListOf()
            mNumbOfFinishedUseCase = 0
            isAnyUseCaseFailed = false
            errorMessage = ""
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

    private fun fetchPopularMovies() {
        val result = fetchPopularMoviesUseCaseSync.fetchPopularMoviesUseCaseSync(page = 1)
        handleResult(result)
    }

    private fun fetchTopRatedMovies() {
        val result = fetchTopRatedMoviesUseCaseSync.fetchTopRatedMoviesUseCaseSync(page = 1)
        handleResult(result)
    }

    private fun fetchUpcomingMovies() {
        val result = fetchUpcomingMoviesUseCaseSync.fetchUpcomingMoviesUseCaseSync(page = 1)
        handleResult(result)
    }

    private fun fetchNowPlayingMovies() {
        val result = fetchNowPlayingMoviesUseCaseSync.fetchNowPlayingMoviesUseCaseSync(page = 1)
        handleResult(result)
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

    private fun handleResult(result: UseCaseSyncResults<MoviesResponse>) {
        synchronized(LOCK) {
            when (result) {
                is UseCaseSyncResults.Results -> {
                    this.movieGroups = this.movieGroups.toMutableList().also {
                        it.add(result.data)
                    }
                }
                is UseCaseSyncResults.Error -> {
                    isAnyUseCaseFailed = true
                    this.errorMessage = result.errMessage
                }
            }
            mNumbOfFinishedUseCase++
            LOCK.notifyAll()
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
