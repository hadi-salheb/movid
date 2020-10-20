package com.hadysalhab.movid.screen.main.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.Review
import com.hadysalhab.movid.movies.ReviewResponse
import com.hadysalhab.movid.movies.usecases.reviews.FetchReviewsUseCase
import java.util.*
import javax.inject.Inject

class ReviewsViewModel @Inject constructor(
    private val fetchReviewsUseCase: FetchReviewsUseCase,
    private val reviewScreenStateManager: ReviewScreenStateManager,
    private val moviesStateManager: MoviesStateManager,
    private val dataValidator: DataValidator
) : ViewModel(), FetchReviewsUseCase.Listener {
    private var movieID: Int = 0
    private var isFirstRender = true
    private lateinit var movieName: String
    private lateinit var reviewsResponse: ReviewResponse
    private val reviews: MutableList<Review> = mutableListOf()
    private val dispatch = reviewScreenStateManager::dispatch

    val state: LiveData<ReviewListState> = reviewScreenStateManager.setInitialStateAndReturn(
        ReviewListState(
            emptyResultsIconDrawable = R.drawable.ic_sad,
            emptyResultsMessage = "No Results Found"
        )
    )
    private val stateValue: ReviewListState
        get() = state.value!!

    init {
        fetchReviewsUseCase.registerListener(this)
    }

    fun onStart(movieID: Int, movieName: String) {
        if (isFirstRender) {
            isFirstRender = false
            this.movieID = movieID
            this.movieName = movieName
            dispatch(ReviewsAction.SetTitle("$movieName (REVIEWS)".toUpperCase(Locale.ROOT)))
            val storedMovie = moviesStateManager.getMovieDetailById(movieID)
            if (dataValidator.isMovieDetailValid(storedMovie)) {
                this.reviewsResponse = storedMovie!!.reviewResponse
                this.reviews.addAll(reviewsResponse.reviews)
                dispatch(
                    ReviewsAction.Success(
                        this.reviews
                    )
                )
            } else {
                dispatch(ReviewsAction.Request)
                fetchApi(1)
            }
        }
    }

    private fun fetchApi(page: Int) {
        fetchReviewsUseCase.fetchReviewsUseCase(
            page,
            this.movieID
        )
    }

    //User Interactions-----------------------------------------------------------------------------
    fun onRetryClicked() {
        dispatch(ReviewsAction.Request)
        fetchApi(1)
    }

    fun loadMore() {
        if (fetchReviewsUseCase.isBusy || this.reviewsResponse.page + 1 > this.reviewsResponse.totalPages) {
            return
        }
        dispatch(ReviewsAction.Pagination)
        fetchApi(this.reviewsResponse.page + 1)
    }

    fun onPaginationErrorClick() {
        dispatch(ReviewsAction.Pagination)
        fetchApi(this.reviewsResponse.page + 1)
    }


    //UseCaseResults--------------------------------------------------------------------------------
    override fun onFetchReviewSuccess(reviews: ReviewResponse) {
        this.reviewsResponse = reviews
        this.reviews.addAll(reviews.reviews)
        dispatch(ReviewsAction.Success(this.reviews))
    }

    override fun onFetchReviewError(err: String) {
        if (stateValue.isPaginationLoading) {
            dispatch(ReviewsAction.PaginationError)
        } else {
            dispatch(ReviewsAction.Error(err))
        }
    }
    //----------------------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        fetchReviewsUseCase.unregisterListener(this)
    }

}