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
    private var page = 1
    private val reviews: MutableList<Review> = mutableListOf()
    private var totalPages = 1
    val viewState: LiveData<ReviewListViewState>
        get() = _viewState


    fun init(movieID: Int) {
        if (this.movieID == null) {
            this.movieID = movieID
            fetchReviewsUseCase.registerListener(this)
        }
        when (viewState.value) {
            null -> {
                fetchReviewsUseCase.fetchReviewsUseCase(
                    page,
                    movieID
                )
            }
        }
    }

    fun loadMore() {
        if (fetchReviewsUseCase.isBusy|| this.page + 1 > this.totalPages) {
            return
        }
        fetchReviewsUseCase.fetchReviewsUseCase(
            ++page,
            movieID!!
        )
    }

    override fun onCleared() {
        super.onCleared()
        fetchReviewsUseCase.unregisterListener(this)
    }

    override fun onFetchReview() {
        if (page == 1) {
            _viewState.value = Loading
        } else {
            _viewState.value = PaginationLoading
        }
    }

    override fun onFetchReviewSuccess(reviewResponse: ReviewResponse) {
        this.totalPages = reviewResponse.totalPages
        this.reviews.addAll(reviewResponse.reviews)
        _viewState.value = ReviewListLoaded(this.reviews)
    }

    override fun onFetchReviewError(err: String) {
        _viewState.value = Error(err)
    }
}