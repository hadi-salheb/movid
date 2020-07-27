package com.hadysalhab.movid.screen.main.featured

import com.hadysalhab.movid.movies.MovieGroup
import com.hadysalhab.movid.movies.MovieGroupType
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class FeaturedView : BaseObservableViewMvc<FeaturedView.Listener>() {
    interface Listener {
        fun onMovieCardClicked(movieID: Int)
        fun onSeeAllClicked(movieGroupType: MovieGroupType)
    }

    abstract fun displayMovieGroups(movieGroups: List<MovieGroup>)
}