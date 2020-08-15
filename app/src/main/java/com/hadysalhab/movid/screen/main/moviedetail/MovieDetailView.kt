package com.hadysalhab.movid.screen.main.moviedetail

import com.hadysalhab.movid.movies.Backdrops
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class MovieDetailView : BaseObservableViewMvc<MovieDetailView.Listener>() {
    interface Listener{

    }
    abstract fun displayMovieDetail(movieDetail:MovieDetail)
    abstract fun displayLoadingScreen()
}