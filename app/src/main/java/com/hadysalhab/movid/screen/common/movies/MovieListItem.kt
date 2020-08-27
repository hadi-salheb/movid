package com.hadysalhab.movid.screen.common.movies

import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class MovieListItem : BaseObservableViewMvc<MovieListItem.Listener>() {
    interface Listener {
        fun onMovieItemClicked(movieID: Int)
    }

    abstract fun displayMovie(movie: Movie)
}