package com.hadysalhab.movid.screen.main.featured

import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class FeaturedScreen : BaseObservableViewMvc<FeaturedScreen.Listener>() {
    interface Listener {
        fun onMovieCardClicked(movieID: Int)
        fun onSeeAllClicked(groupType: GroupType)
        fun onCountryToolbarItemClicked(toolbarCountryItem: ToolbarCountryItems)
        fun onOverflowMenuIconClick()
        fun onBackgroundClick()
        fun onRetryClicked()
        fun onRefresh()
    }

    abstract fun displayFeaturedMovies(featured: List<MoviesResponse>)
    abstract fun showLoadingIndicator()
    abstract fun hideLoadingIndicator()
    abstract fun showRefreshIndicator()
    abstract fun hideRefreshIndicator()
    abstract fun showPowerMenu()
    abstract fun hidePowerMenu()
    abstract fun setPowerMenuItem(toolbarCountryItem: ToolbarCountryItems)
    abstract fun disablePullRefresh()
    abstract fun enablePullRefresh()
    abstract fun showErrorScreen(error: String)
    abstract fun hideErrorScreen()
}