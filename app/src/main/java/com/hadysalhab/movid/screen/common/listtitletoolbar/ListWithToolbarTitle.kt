package com.hadysalhab.movid.screen.common.listtitletoolbar

import androidx.annotation.DrawableRes
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class ListWithToolbarTitleState(
    val isLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val isPaginationError: Boolean = false,
    val data: List<Movie> = emptyList(),
    val error: String? = null,
    val title: String = "",
    @DrawableRes val emptyResultsIconDrawable: Int,
    val emptyResultsMessage: String
)

abstract class ListWithToolbarTitle : BaseObservableViewMvc<ListWithToolbarTitle.Listener>() {
    interface Listener {
        fun onMovieItemClicked(movieID: Int)
        fun loadMoreItems()
        fun onRetryClicked()
        fun onPaginationErrorClicked()
    }

    abstract fun handleScreenState(screenState: ListWithToolbarTitleState)
}