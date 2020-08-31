package com.hadysalhab.movid.movies.usecases.list

import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.*
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

abstract class FetchMovieListUseCase(
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val timeProvider: TimeProvider,
    private val errorMessageHandler: ErrorMessageHandler,
    private val dataValidator: DataValidator,
    private val moviesStateManager: MoviesStateManager
) : BaseBusyObservable<FetchMovieListUseCase.Listener>() {
    interface Listener {
        fun onFetching()
        fun onFetchSuccess(movies: List<Movie>)
        fun onFetchError(err: String)
    }

    private var page = 1
    private lateinit var moviesResponse: MoviesResponse
    private lateinit var groupType: GroupType

    fun fetchMovieListUseCase(region: String, pageToFetch: Int, movieID: Int?) {
        if (this::moviesResponse.isInitialized && pageToFetch > moviesResponse.total_pages) {
            return
        }
        this.groupType = getGroupType()
        this.page = pageToFetch
        if (isMoviePartOfTheMovieStore() && page == 1) {
            val moviesResponse = moviesStateManager.getMoviesResponseByGroupType(groupType)
            if (dataValidator.isMoviesResponseValid(moviesResponse)) {
                this.moviesResponse = moviesResponse.deepCopy()
                notifySuccess()
                return
            }
        }
        assertNotBusyAndBecomeBusy()
        backgroundThreadPoster.post {
            notifyLoading()
            when (val response = fetchMovieList(region, page, movieID)) {
                is ApiSuccessResponse -> {
                    val movieGroup =
                        schemaToModelHelper.getMoviesResponseFromSchema(groupType, response.body)
                    if (page == 1) {
                        this.moviesResponse = movieGroup
                        if (isMoviePartOfTheMovieStore()) {
                            this.moviesResponse.apply {
                                timeStamp = timeProvider.currentTimestamp
                            }
                            moviesStateManager.updateMoviesResponseByGroupType(
                                this.moviesResponse,
                                groupType
                            )
                        }
                    } else {
                        val movies = mutableListOf<Movie>()
                        movies.addAll(this.moviesResponse.movies!!)
                        movies.addAll(movieGroup.movies ?: emptyList())
                        this.moviesResponse = this.moviesResponse.withMovies(movies)
                    }
                    notifySuccess()
                }
                is ApiEmptyResponse -> {
                    notifyFailure(errorMessageHandler.createErrorMessage(""))
                }
                is ApiErrorResponse -> {
                    notifyFailure(errorMessageHandler.createErrorMessage(response.errorMessage))

                }
            }
        }
    }

    private fun notifyLoading() {
        uiThreadPoster.post {
            listeners.forEach { it.onFetching() }
        }
    }

    private fun notifySuccess() {
        uiThreadPoster.post {
            listeners.forEach { it.onFetchSuccess(this.moviesResponse.movies ?: emptyList()) }
            becomeNotBusy()
        }
    }

    private fun notifyFailure(err: String) {
        uiThreadPoster.post {
            listeners.forEach { it.onFetchError(err) }
            becomeNotBusy()
        }
    }

    protected abstract fun fetchMovieList(
        region: String,
        page: Int,
        movieID: Int?
    ): ApiResponse<MoviesResponseSchema>

    protected abstract fun getGroupType(): GroupType

    protected open fun isMoviePartOfTheMovieStore(): Boolean {
        return false
    } //hooks
}