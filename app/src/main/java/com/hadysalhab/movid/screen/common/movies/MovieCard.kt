package com.hadysalhab.movid.screen.common.movies

import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class MovieCard : BaseObservableViewMvc<MovieCard.Listener>() {
    interface Listener {
        fun onMovieCardClicked(movieID: Int)
    }

    abstract fun displayMovie(movie: Movie)
}