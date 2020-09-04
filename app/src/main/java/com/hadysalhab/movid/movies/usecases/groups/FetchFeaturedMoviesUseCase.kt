package com.hadysalhab.movid.movies.usecases.groups


import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.usecases.factory.BaseFeaturedMoviesUseCaseFactory
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchFeaturedMoviesUseCase(
    private val baseFeaturedMoviesUseCaseFactory: BaseFeaturedMoviesUseCaseFactory,
    private val deviceConfigManager: DeviceConfigManager,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val errorMessageHandler: ErrorMessageHandler,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val uiThreadPoster: UiThreadPoster
) :
    BaseBusyObservable<FetchFeaturedMoviesUseCase.Listener>() {
    interface Listener {
        fun onFetchMovieGroupsSucceeded(movieGroups: List<MoviesResponse>)
        fun onFetchMovieGroupsFailed(msg: String)
    }

    private var mNumbOfFinishedUseCase = 0
    private var isAnyUseCaseFailed = false
    private val LOCK = Object()
    private lateinit var movieGroups: List<MoviesResponse>
    private lateinit var errorMessage: String
    private val featured =
        arrayOf(GroupType.POPULAR, GroupType.TOP_RATED, GroupType.UPCOMING, GroupType.NOW_PLAYING)

    fun fetchFeaturedMoviesUseCaseAndNotify() {
        assertNotBusyAndBecomeBusy()
        synchronized(LOCK) {
            movieGroups = mutableListOf()
            mNumbOfFinishedUseCase = 0
            isAnyUseCaseFailed = false
            errorMessage = ""
        }
        backgroundThreadPoster.post {
            waitForAllUseCasesToFinish()
        }
        featured.forEach { groupType ->
            backgroundThreadPoster.post {
                val result =
                    baseFeaturedMoviesUseCaseFactory.createBaseFeaturedMoviesUseCase(groupType)
                        .fetchFeaturedMoviesUseCase(
                            page = 1,
                            region = deviceConfigManager.getISO3166CountryCodeOrUS()
                        )
                handleResult(groupType, result)
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

    private fun handleResult(groupType: GroupType, response: ApiResponse<MoviesResponseSchema>) {
        synchronized(LOCK) {
            when (response) {
                is ApiSuccessResponse -> {
                    this.movieGroups = this.movieGroups.toMutableList().also {
                        it.add(
                            schemaToModelHelper.getMoviesResponseFromSchema(
                                groupType,
                                response.body
                            )
                        )
                    }
                }
                is ApiErrorResponse, is ApiEmptyResponse -> {
                    isAnyUseCaseFailed = true
                    this.errorMessage = errorMessageHandler.getErrorMessageFromApiResponse(response)
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
