package com.hadysalhab.movid.screen.common.movielist

import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class MovieListView : BaseObservableViewMvc<MovieListView.Listener>() {
    interface Listener {
        fun onMovieItemClicked(movieID: Int)
        fun loadMoreItems()
    }

    abstract fun displayMovies(movies: List<Movie>)
    abstract fun displayPaginationLoading()
    abstract fun displayLoadingIndicator()
    abstract fun displayEmptyListIndicator(msg: String)
}