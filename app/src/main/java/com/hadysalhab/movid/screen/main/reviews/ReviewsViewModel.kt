package com.hadysalhab.movid.screen.main.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.movies.Review
import com.hadysalhab.movid.movies.ReviewResponse
import com.hadysalhab.movid.movies.usecases.reviews.FetchReviewsUseCase
import javax.inject.Inject

class ReviewsViewModel @Inject constructor(
    private val fetchReviewsUseCase: FetchReviewsUseCase
) : ViewModel(), FetchReviewsUseCase.Listener {
    private val _viewState = MutableLiveData<ReviewListViewState>()
    private var movieID: Int? = null
    private lateinit var reviewsResponse: ReviewResponse
    private val reviews: MutableList<Review> = mutableListOf()
    val viewState: LiveData<ReviewListViewState>
        get() = _viewState

    fun init(movieID: Int) {
        if (this.movieID == null) {
            this.movieID = movieID
            fetchReviewsUseCase.registerListener(this)
        }
        when (viewState.value) {
            null -> {
                _viewState.value = Loading
                fetchReviewsUseCase.fetchReviewsUseCase(
                    1,
                    movieID
                )
            }
        }
    }

    fun loadMore() {
        if (fetchReviewsUseCase.isBusy || this.reviewsResponse.page + 1 > this.reviewsResponse.totalPages) {
            return
        }
        _viewState.value = PaginationLoading
        fetchReviewsUseCase.fetchReviewsUseCase(
            this.reviewsResponse.page + 1,
            movieID!!
        )
    }

    //UseCaseResults--------------------------------------------------------------------------------

    override fun onFetchReviewSuccess(apiReviewResponse: ReviewResponse) {
        this.reviewsResponse = apiReviewResponse
        this.reviews.addAll(apiReviewResponse.reviews)
        _viewState.value = ReviewListLoaded(this.reviews)
    }

    override fun onFetchReviewError(err: String) {
        _viewState.value = Error(err)
    }
    //----------------------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        fetchReviewsUseCase.unregisterListener(this)
    }


}