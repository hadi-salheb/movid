package com.hadysalhab.movid.screen.main.moviedetail

import com.hadysalhab.movid.movies.Backdrops
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class MovieDetailView : BaseObservableViewMvc<MovieDetailView.Listener>() {
    interface Listener{

    }
    abstract fun displayCarouselImages ( backdrops:List<Backdrops>)
    abstract fun displayPosterImage(posterPath:String?)
}