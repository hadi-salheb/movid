package com.hadysalhab.movid.movies.usecases.reviews

import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.*
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchReviewsUseCase(
    private val fetchReviewsUseCaseSync: FetchReviewsUseCaseSync,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val errorMessageHandler: ErrorMessageHandler,
    private val dataValidator: DataValidator,
    private val moviesStateManager: MoviesStateManager
) : BaseBusyObservable<FetchReviewsUseCase.Listener>() {
    interface Listener {
        fun onFetching()
        fun onFetchSuccess(reviews: List<Review>)
        fun onFetchError(err: String)
    }

    private var page = 1
    lateinit var reviewResponse: ReviewResponse

    fun fetchReviewsUseCase(pageToFetch: Int, movieID: Int) {
        if (this::reviewResponse.isInitialized && pageToFetch > reviewResponse.totalPages) {
            return
        }
        this.page = pageToFetch
        val movieDetail = moviesStateManager.getMovieDetailById(movieID)
        if (page == 1 && dataValidator.isMovieDetailValid(movieDetail)) {
            this.reviewResponse = movieDetail!!.reviewResponse
            notifySuccess()
            return
        }
        assertNotBusyAndBecomeBusy()
        backgroundThreadPoster.post {
            notifyLoading()
            when (val response = fetchReviewsUseCaseSync.fetchReviewsUseCaseSync(page, movieID)) {
                is ApiSuccessResponse -> {
                    val reviewResponse =
                        schemaToModelHelper.getReviewsResponseFromSchema(response.body)
                    if (page == 1) {
                        this.reviewResponse = reviewResponse
                    } else {
                        val reviews = mutableListOf<Review>()
                        reviews.addAll(this.reviewResponse.reviews)
                        reviews.addAll(reviewResponse.reviews)
                        this.reviewResponse.reviews = reviews
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
            listeners.forEach { it.onFetchSuccess(this.reviewResponse.reviews) }
            becomeNotBusy()
        }
    }

    private fun notifyFailure(err: String) {
        uiThreadPoster.post {
            listeners.forEach { it.onFetchError(err) }
            becomeNotBusy()
        }
    }
}