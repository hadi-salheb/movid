package com.hadysalhab.movid.screen.main.featured

import com.hadysalhab.movid.movies.GroupType
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

    abstract fun handleState(featuredState: FeaturedScreenState)
}