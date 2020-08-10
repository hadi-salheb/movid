package com.hadysalhab.movid.screen.main.featured

import com.hadysalhab.movid.movies.MovieGroup
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class FeaturedView : BaseObservableViewMvc<FeaturedView.Listener>() {
    interface Listener {
        fun onMovieCardClicked(movieID: Int)
        fun onSeeAllClicked(groupType: GroupType)
    }

    abstract fun displayMovieGroups(movieGroups: List<MovieGroup>)
    abstract fun displayLoadingScreen()
}