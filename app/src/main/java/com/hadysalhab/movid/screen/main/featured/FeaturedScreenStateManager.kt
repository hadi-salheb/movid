package com.hadysalhab.movid.screen.main.featured

import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.movies.MoviesResponse

data class FeaturedScreenState(
    val isLoading: Boolean = false,
    val isPowerMenuOpen: Boolean = false,
    val powerMenuItem: ToolbarCountryItems = ToolbarCountryItems.AUSTRALIA,
    val isRefreshing: Boolean = false,
    val data: List<MoviesResponse> = emptyList(),
    val errorMessage: String? = null
)

sealed class FeaturedActions {
    data class FeaturedRequest(val toolbarCountryItems: ToolbarCountryItems) : FeaturedActions()
    object FeaturedRefresh : FeaturedActions()
    data class FeaturedSuccess(val data: List<MoviesResponse>) : FeaturedActions()
    object FeaturedRefreshError : FeaturedActions()
    data class FeaturedError(val errorMessage: String) : FeaturedActions()
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

            is FeaturedActions.FeaturedRequest -> {
                state = state.copy(
                    isLoading = true,
                    isRefreshing = false,
                    errorMessage = null,
                    isPowerMenuOpen = false,
                    data = emptyList(),
                    powerMenuItem = featuredActions.toolbarCountryItems
                )
            }
            is FeaturedActions.FeaturedRefresh -> {
                state = state.copy(
                    isLoading = false,
                    isRefreshing = true,
                    errorMessage = null,
                    isPowerMenuOpen = false
                )
            }
            is FeaturedActions.FeaturedSuccess -> {
                state = state.copy(
                    isLoading = false,
                    isRefreshing = false,
                    data = featuredActions.data,
                    errorMessage = null
                )
            }
            is FeaturedActions.FeaturedRefreshError -> {
                state = state.copy(
                    isRefreshing = false
                )
            }
            is FeaturedActions.FeaturedError -> {
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