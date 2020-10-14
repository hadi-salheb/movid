package com.hadysalhab.movid.movies.usecases.reviews

import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.ReviewResponse
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.ReviewsSchema
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchReviewsUseCase(
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val errorMessageHandler: ErrorMessageHandler,
    private val tmdbApi: TmdbApi
) : BaseBusyObservable<FetchReviewsUseCase.Listener>() {
    interface Listener {
        fun onFetchReviewSuccess(reviews: ReviewResponse)
        fun onFetchReviewError(err: String)
    }

    fun fetchReviewsUseCase(pageToFetch: Int, movieID: Int) {
        assertNotBusyAndBecomeBusy()
        fireNetworkRequest(pageToFetch, movieID)
    }

    private fun fireNetworkRequest(page: Int, movieID: Int) {
        backgroundThreadPoster.post {
            when (val response = fetchReviewsUseCaseSync(page, movieID)) {
                is ApiSuccessResponse -> {
                    val reviewResponse =
                        schemaToModelHelper.getReviewsResponseFromSchema(response.body)
                    notifySuccess(reviewResponse)
                }
                is ApiEmptyResponse, is ApiErrorResponse -> {
                    notifyFailure(errorMessageHandler.getErrorMessageFromApiResponse(response))
                }
            }
        }
    }

    private fun fetchReviewsUseCaseSync(page: Int, movieID: Int): ApiResponse<ReviewsSchema> = try {
        val res = tmdbApi.fetchReviewsForMovie(movieID, page).execute()
        ApiResponse.create<ReviewsSchema>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }

    private fun notifySuccess(reviews: ReviewResponse) {
        uiThreadPoster.post {
            listeners.forEach { it.onFetchReviewSuccess(reviews) }
            becomeNotBusy()
        }
    }

    private fun notifyFailure(err: String) {
        uiThreadPoster.post {
            listeners.forEach { it.onFetchReviewError(err) }
            becomeNotBusy()
        }
    }
}