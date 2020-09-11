package com.hadysalhab.movid.screen.main.featured

import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class FeaturedView : BaseObservableViewMvc<FeaturedView.Listener>() {
    interface Listener {
        fun onMovieCardClicked(movieID: Int)
        fun onSeeAllClicked(groupType: GroupType)
        fun onCountryToolbarItemClicked(toolbarCountryItem: ToolbarCountryItems)
    }

    abstract fun displayMovieGroups(movieGroups: List<MoviesResponse>)
    abstract fun displayLoadingScreen()
    abstract fun getSelectedCountry(): ToolbarCountryItems
    abstract fun disablePopupMenu()
    abstract fun enablePopupMenu()
}