package com.hadysalhab.movid.screen.main.featuredgroups

import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.movies.MoviesResponse


sealed class FeaturedActions {
    data class Request(val toolbarCountryItems: ToolbarCountryItems) : FeaturedActions()
    object Refresh : FeaturedActions()
    data class Success(val data: List<MoviesResponse>) : FeaturedActions()
    object RefreshError : FeaturedActions()
    data class Error(val errorMessage: String) : FeaturedActions()
    object Reset : FeaturedActions()
    object OpenPowerMenu : FeaturedActions()
    object ClosePowerMenu : FeaturedActions()
    object TogglePowerMenuVisibility : FeaturedActions()
}

class FeaturedScreenStateManager {
    val stateLiveData = MutableLiveData<FeaturedScreenState>(FeaturedScreenState())
    private var state: FeaturedScreenState
        get() {
            return stateLiveData.value!!
        }
        set(value) {
            stateLiveData.value = value
        }

    fun dispatch(featuredActions: FeaturedActions) {
        when (featuredActions) {
            is FeaturedActions.TogglePowerMenuVisibility -> {
                state = state.copy(isPowerMenuOpen = !state.isPowerMenuOpen)
            }

            is FeaturedActions.Request -> {
                state = state.copy(
                    isLoading = true,
                    isRefreshing = false,
                    errorMessage = null,
                    isPowerMenuOpen = false,
                    data = emptyList(),
                    powerMenuItem = featuredActions.toolbarCountryItems
                )
            }
            is FeaturedActions.Refresh -> {
                state = state.copy(
                    isLoading = false,
                    isRefreshing = true,
                    errorMessage = null,
                    isPowerMenuOpen = false
                )
            }
            is FeaturedActions.Success -> {
                state = state.copy(
                    isLoading = false,
                    isRefreshing = false,
                    data = featuredActions.data,
                    errorMessage = null
                )
            }
            is FeaturedActions.RefreshError -> {
                state = state.copy(
                    isRefreshing = false
                )
            }
            is FeaturedActions.Error -> {
                state = state.copy(
                    errorMessage = featuredActions.errorMessage,
                    isRefreshing = false,
                    isLoading = false,
                    data = emptyList()
                )
            }
            is FeaturedActions.OpenPowerMenu -> {
                state = state.copy(
                    isPowerMenuOpen = true
                )
            }
            is FeaturedActions.ClosePowerMenu -> {
                state = state.copy(
                    isPowerMenuOpen = false
                )
            }
            is FeaturedActions.Reset -> {
                state = FeaturedScreenState()
            }
        }
    }
}