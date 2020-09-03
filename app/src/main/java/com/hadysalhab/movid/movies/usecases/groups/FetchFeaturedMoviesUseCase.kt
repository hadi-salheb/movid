package com.hadysalhab.movid.movies.usecases.groups


import com.hadysalhab.movid.common.usecases.UseCaseSyncResults
import com.hadysalhab.movid.common.usecases.factory.BaseFeaturedMoviesUseCaseFactory
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchFeaturedMoviesUseCase(
    private val baseFeaturedMoviesUseCaseFactory: BaseFeaturedMoviesUseCaseFactory,
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
    private val featured =
        arrayOf(GroupType.POPULAR, GroupType.TOP_RATED, GroupType.UPCOMING, GroupType.NOW_PLAYING)

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
        featured.forEach {
            backgroundThreadPoster.post {
                val result = baseFeaturedMoviesUseCaseFactory.createBaseFeaturedMoviesUseCase(it)
                    .fetchFeaturedMoviesUseCase(page = 1)
                handleResult(result)
            }
        }
    }

    private fun waitForAllUseCasesToFinish() {
        synchronized(LOCK) {
            while (mNumbOfFinishedUseCase < featured.size && !isAnyUseCaseFailed) {
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
