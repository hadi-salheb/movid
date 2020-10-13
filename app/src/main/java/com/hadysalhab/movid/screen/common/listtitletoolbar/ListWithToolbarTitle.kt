package com.hadysalhab.movid.screen.common.listtitletoolbar

import androidx.annotation.DrawableRes
import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class ListWithToolbarTitleState(
    val movieListScreenState: MovieListScreenState,
    val title: String = "",
    @DrawableRes val menuIcon: Int? = null

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