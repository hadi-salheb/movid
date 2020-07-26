package com.hadysalhab.movid.screen.common.components.moviecard

import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class MovieCard : BaseObservableViewMvc<MovieCard.Listener>() {
    interface Listener {
        fun onMovieCardClicked(movieID: Int)
    }

    abstract fun setMovieImage(image: String)
    abstract fun setMovieTitle(title: String)
}