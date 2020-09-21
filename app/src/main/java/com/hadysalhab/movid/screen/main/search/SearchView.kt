package com.hadysalhab.movid.screen.main.search

import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class SearchView : BaseObservableViewMvc<SearchView.Listener>() {
    abstract fun displayLoadingIndicator()
    abstract fun displayPaginationLoading()
    abstract fun displayMovies(movies: List<Movie>)
    abstract fun renderGenres()
    abstract fun renderMovies()
    abstract fun displayEmptyListIndicator(msg: String)

    interface Listener {
        fun onSearchConfirmed(text: CharSequence)
        fun onSearchBackBtnClick()
        fun loadMoreItems()
        fun onMovieItemClicked(movieID: Int)
    }
}