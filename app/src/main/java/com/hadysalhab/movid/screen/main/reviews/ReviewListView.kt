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
        fun onRetryClicked()
        fun onPaginationErrorClicked()
    }

    protected abstract fun hideLoadingIndicator()
    protected abstract fun showLoadingIndicator()
    protected abstract fun showErrorScreen(msg: String)
    protected abstract fun hideErrorScreen()
    protected abstract fun showEmptyDataScreen(@DrawableRes icon: Int, msg: String)
    protected abstract fun hideEmptyDataScreen()
    protected abstract fun showPagination(data: List<Review>)
    protected abstract fun showPaginationError(data: List<Review>)
    protected abstract fun showData(data: List<Review>)
    abstract fun handleState(reviewListState: ReviewListState)
}