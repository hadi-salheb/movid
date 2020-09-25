package com.hadysalhab.movid.screen.main.featured

import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class FeaturedView : BaseObservableViewMvc<FeaturedView.Listener>() {
    interface Listener {
        fun onMovieCardClicked(movieID: Int)
        fun onSeeAllClicked(groupType: GroupType)
        fun onCountryToolbarItemClicked(toolbarCountryItem: ToolbarCountryItems)
        fun onOverflowMenuIconClick()
        fun onBackgroundClick()
        fun onRetryClicked()
    }

    abstract fun displayMovieGroups(movieGroups: List<MoviesResponse>)
    abstract fun showLoadingIndicator()
    abstract fun hideLoadingIndicator()
    abstract fun showPowerMenu()
    abstract fun hidePowerMenu()
    abstract fun setPowerMenuItem(powerMenuItem: ToolbarCountryItems)
    abstract fun showErrorScreen(errorMessage: String)
    abstract fun hideErrorScreen()
}