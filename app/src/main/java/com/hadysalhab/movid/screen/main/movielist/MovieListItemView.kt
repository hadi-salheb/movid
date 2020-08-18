package com.hadysalhab.movid.screen.main.movielist

import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class MovieListItemView : BaseObservableViewMvc<MovieListItemView.Listener>() {
    interface Listener {
        fun onMovieItemClicked(movieID: Int)
    }

    abstract fun displayMovie(movie: Movie)
}