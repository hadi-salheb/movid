package com.hadysalhab.movid.screen.main.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.Review
import com.hadysalhab.movid.movies.usecases.list.FetchMovieListUseCase
import com.hadysalhab.movid.movies.usecases.list.FetchMovieListUseCaseFactory
import com.hadysalhab.movid.movies.usecases.reviews.FetchReviewsUseCase
import javax.inject.Inject

class ReviewsViewModel @Inject constructor(
    private val fetchReviewsUseCase: FetchReviewsUseCase
) : ViewModel(), FetchReviewsUseCase.Listener {
    private val _viewState = MutableLiveData<ReviewListViewState>()
    private var movieID: Int? = null
    private var page = 1
    val viewState: LiveData<ReviewListViewState>
        get() = _viewState


    fun init( movieID: Int) {
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
        if (fetchReviewsUseCase.isBusy) {
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

    override fun onFetching() {
        if (page == 1) {
            _viewState.value = Loading
        } else {
            _viewState.value = PaginationLoading
        }
    }

    override fun onFetchSuccess(reviews: List<Review>) {
        _viewState.value = ReviewListLoaded(reviews)
    }

    override fun onFetchError(err: String) {
        _viewState.value = Error(err)
    }
}