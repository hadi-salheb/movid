package com.hadysalhab.movid.screen.main.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.movies.Review


sealed class ReviewsAction {
    data class SetTitle(val title: String) : ReviewsAction()
    object Request : ReviewsAction()
    data class Success(val reviews: List<Review>) : ReviewsAction()
    data class Error(val msg: String) : ReviewsAction()
    object PaginationError : ReviewsAction()
    object Pagination : ReviewsAction()
}


class ReviewScreenStateManager {
    private val stateLiveData = MutableLiveData<ReviewListState>()

    fun setInitialStateAndReturn(initialState: ReviewListState): LiveData<ReviewListState> {
        stateLiveData.value = initialState
        return stateLiveData
    }

    private var state: ReviewListState
        get() {
            return stateLiveData.value!!
        }
        set(value) {
            stateLiveData.value = value
        }

    fun dispatch(reviewsAction: ReviewsAction) {
        state = listWithToolbarTitleReducer(reviewsAction)
    }

    private fun listWithToolbarTitleReducer(reviewsAction: ReviewsAction) =
        when (reviewsAction) {
            is ReviewsAction.SetTitle -> {
                state.copy(
                    title = reviewsAction.title
                )
            }
            is ReviewsAction.Request -> {
                state.copy(
                    data = emptyList(),
                    isLoading = true,
                    errorMessage = null
                )
            }
            is ReviewsAction.Success -> {
                val reviews = mutableListOf<Review>()
                reviews.addAll(reviewsAction.reviews)
                state.copy(
                    isLoading = false,
                    isPaginationLoading = false,
                    errorMessage = null,
                    data = reviews
                )
            }
            is ReviewsAction.Pagination -> {
                state.copy(
                    isPaginationLoading = true,
                    paginationError = false
                )
            }
            is ReviewsAction.Error -> {
                state.copy(
                    data = emptyList(),
                    isLoading = false,
                    errorMessage = reviewsAction.msg
                )
            }
            is ReviewsAction.PaginationError -> {
                state.copy(
                    isPaginationLoading = false,
                    paginationError = true
                )
            }
        }
}