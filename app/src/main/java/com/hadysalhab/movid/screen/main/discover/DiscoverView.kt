package com.hadysalhab.movid.screen.main.discover

import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc
import com.hadysalhab.movid.screen.main.search.Genre

abstract class DiscoverView : BaseObservableViewMvc<DiscoverView.Listener>() {
    interface Listener {
        fun loadMoreItems()
        fun onMovieItemClicked(movieID: Int)
    }

    abstract fun displayLoadingIndicator()
    abstract fun displayPaginationLoading()
    abstract fun displayMovies(movies: List<Movie>)
    abstract fun displayEmptyListIndicator(msg: String)
    abstract fun setGenreDetail(genre: Genre)


}