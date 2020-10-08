package com.hadysalhab.movid.screen.main.featuredgroups

import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class FeaturedScreenState(
    val isLoading: Boolean = false,
    val isPowerMenuOpen: Boolean = false,
    val powerMenuItem: ToolbarCountryItems = ToolbarCountryItems.AUSTRALIA,
    val isRefreshing: Boolean = false,
    val data: List<MoviesResponse> = emptyList(),
    val errorMessage: String? = null
)

abstract class FeaturedGroupScreen : BaseObservableViewMvc<FeaturedGroupScreen.Listener>() {
    interface Listener {
        fun onMovieCardClicked(movieID: Int)
        fun onSeeAllClicked(groupType: GroupType)
        fun onCountryToolbarItemClicked(toolbarCountryItem: ToolbarCountryItems)
        fun onOverflowMenuIconClick()
        fun onBackgroundClick()
        fun onRetryClicked()
        fun onRefresh()
    }

    protected abstract fun displayFeaturedMovies(featured: List<MoviesResponse>)
    protected abstract fun showLoadingIndicator()
    protected abstract fun hideLoadingIndicator()
    protected abstract fun showRefreshIndicator()
    protected abstract fun hideRefreshIndicator()
    protected abstract fun showPowerMenu()
    protected abstract fun hidePowerMenu()
    protected abstract fun setPowerMenuItem(toolbarCountryItem: ToolbarCountryItems)
    protected abstract fun disablePullRefresh()
    protected abstract fun enablePullRefresh()
    protected abstract fun showErrorScreen(error: String)
    protected abstract fun hideErrorScreen()
    abstract fun handleScreenState(featuredScreenState: FeaturedScreenState)
}