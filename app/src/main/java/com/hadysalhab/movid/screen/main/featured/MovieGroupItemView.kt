package com.hadysalhab.movid.screen.main.featured

import com.hadysalhab.movid.movies.MovieGroup
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class MovieGroupItemView : BaseObservableViewMvc<MovieGroupItemView.Listener>(){
    interface Listener{
       fun onMovieCardClicked(movieID: Int)
       fun onSeeAllClicked()
    }
    abstract fun displayMovieGroup(movieGroup:MovieGroup)
}