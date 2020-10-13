package com.hadysalhab.movid.screen.main.search

import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class SearchScreenState(
    val movieListScreenState: MovieListScreenState?
)

abstract class SearchView : BaseObservableViewMvc<SearchView.Listener>() {
    interface Listener {
        fun onSearchConfirmed(query: String)
        fun onSearchBackBtnClick()
        fun loadMoreItems()
        fun onMovieItemClicked(movieID: Int)
        fun onGenreListItemClick(genre: Genre)
        fun onRetryClicked()
        fun onPaginationErrorClicked()
    }

    abstract fun handleState(searchScreenState: SearchScreenState)
}