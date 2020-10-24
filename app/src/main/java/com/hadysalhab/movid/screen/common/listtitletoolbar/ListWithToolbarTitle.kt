package com.hadysalhab.movid.screen.common.listtitletoolbar

import androidx.annotation.DrawableRes
import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class ListWithToolbarTitleState(
    val movieListScreenState: MovieListScreenState,
    val title: String = "",
    @DrawableRes val menuIcon: Int? = null,
    val showBackArrow: Boolean = false,
    val showLoginRequired: Boolean = false,
    val loginRequiredText: String? = null
)

abstract class ListWithToolbarTitle : BaseObservableViewMvc<ListWithToolbarTitle.Listener>() {
    interface Listener {
        fun onMovieItemClicked(movieID: Int)
        fun loadMoreItems()
        fun onRetryClicked()
        fun onPaginationErrorClicked()
        fun onMenuIconClicked()
        fun onBackArrowClick()
        fun onLoginRequiredBtnClicked()
    }

    abstract fun handleScreenState(screenState: ListWithToolbarTitleState)
}