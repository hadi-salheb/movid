package com.hadysalhab.movid.screen.main.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.movies.MoviesResponse

class FeaturedScreenStateManager {
    private val _viewState =
        MutableLiveData<FeaturedViewState>(FeaturedViewState(powerMenuItem = ToolbarCountryItems.AUSTRALIA))
    val viewState: LiveData<FeaturedViewState>
        get() = _viewState
    private var state: FeaturedViewState
        get() {
            return _viewState.value!!
        }
        set(value) {
            _viewState.value = value
        }

    private var checkingForUpdates = false


    fun updatePowerMenuItem(storedFeaturedPowerMenuItem: ToolbarCountryItems) {
        state = state.copy(powerMenuItem = storedFeaturedPowerMenuItem)
    }

    fun showUserFeaturedMovies(moviesResponse: List<MoviesResponse>) {
        state = state.copy(
            isLoading = false,
            data = moviesResponse,
            errorMessage = null
        )
    }

    fun showUserLoadingScreen() {
        state = state.copy(
            isLoading = true,
            errorMessage = null,
            data = emptyList(),
            isPowerMenuOpen = false
        )
    }

    fun showUserLoadingScreenWithNewPowerItem(toolbarCountryItem: ToolbarCountryItems) {
        state = state.copy(
            isLoading = true,
            data = emptyList(),
            errorMessage = null,
            powerMenuItem = toolbarCountryItem,
            isPowerMenuOpen = false
        )
    }

    fun showUserCheckingForUpdates() {
        checkingForUpdates = true
        state = state.copy(
            isLoading = true,
            errorMessage = null,
            isPowerMenuOpen = false
        )
    }

    fun showUserCheckingForUpdatesSuccess(moviesResponse: List<MoviesResponse>) {
        checkingForUpdates = false
        state = state.copy(
            isLoading = false,
            data = moviesResponse,
            errorMessage = null
        )
    }

    fun showUserCheckingForUpdatesFailure() {
        // No need to show error Message
        checkingForUpdates = false
        state = state.copy(
            isLoading = false
        )
    }

    fun togglePowerMenuVisibility() {
        state = state.copy(isPowerMenuOpen = !state.isPowerMenuOpen)
    }

    fun closePowerMenu() {
        state = state.copy(isPowerMenuOpen = false)
    }

    fun showUserErrorScreen(errorMessage: String) {
        state = state.copy(isLoading = false, errorMessage = errorMessage, data = emptyList())
    }


    fun areFeaturedMoviesDisplayed() = !state.data.isNullOrEmpty()
    fun getCurrentPowerMenuItem(): ToolbarCountryItems = state.powerMenuItem
    fun getCurrentDisplayedMovies(): List<MoviesResponse> = state.data
    fun isPowerMenuOpen(): Boolean = state.isPowerMenuOpen
    fun isCheckingForUpdate(): Boolean = checkingForUpdates


}