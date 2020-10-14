package com.hadysalhab.movid.screen.main.reviews

import androidx.annotation.DrawableRes
import com.hadysalhab.movid.movies.Review
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class ReviewListState(
    val isLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val data: List<Review> = emptyList(),
    @DrawableRes val emptyResultsIconDrawable: Int,
    val emptyResultsMessage: String,
    val errorMessage: String? = null,
    val paginationError: Boolean = false,
    val title: String = ""
)

abstract class ReviewListView : BaseObservableViewMvc<ReviewListView.Listener>() {
    interface Listener {
        fun loadMoreItems()
    }

    abstract fun displayReviews(reviews: List<Review>)
    abstract fun displayPaginationLoading()
    abstract fun displayLoadingIndicator()
}