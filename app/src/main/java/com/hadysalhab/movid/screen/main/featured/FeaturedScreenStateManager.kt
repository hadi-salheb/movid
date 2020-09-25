package com.hadysalhab.movid.screen.main.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.movies.MoviesResponse

class FeaturedScreenStateManager {
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isPowerMenuOpen = MutableLiveData<Boolean>(false)
    val isPowerMenuOpen: LiveData<Boolean>
        get() = _isPowerMenuOpen

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    private val _data = MutableLiveData<List<MoviesResponse>>(emptyList())
    val data: LiveData<List<MoviesResponse>>
        get() = _data

    private val _powerMenuItem = MutableLiveData<ToolbarCountryItems>(ToolbarCountryItems.AUSTRALIA)
    val powerMenuItem: LiveData<ToolbarCountryItems>
        get() = _powerMenuItem


    private var checkingForUpdates = false


    fun updatePowerMenuItem(storedFeaturedPowerMenuItem: ToolbarCountryItems) {
        _powerMenuItem.value = storedFeaturedPowerMenuItem
    }

    fun showUserFeaturedMovies(moviesResponse: List<MoviesResponse>) {
        _isLoading.value = false
        _data.value = moviesResponse
        _errorMessage.value = null
    }

    fun showUserLoadingScreen() {
        _isLoading.value = true
        _data.value = emptyList()
        _errorMessage.value = null
        _isPowerMenuOpen.value = false
    }

    fun showUserLoadingScreenWithNewPowerItem(toolbarCountryItem: ToolbarCountryItems) {
        _isLoading.value = true
        _data.value = emptyList()
        _errorMessage.value = null
        _isPowerMenuOpen.value = false
        _powerMenuItem.value = toolbarCountryItem
        _isPowerMenuOpen.value = false
    }

    fun showUserCheckingForUpdates() {
        checkingForUpdates = true
        _isLoading.value = true
        _isPowerMenuOpen.value = false
        _errorMessage.value = null
    }

    fun showUserCheckingForUpdatesSuccess(moviesResponse: List<MoviesResponse>) {
        checkingForUpdates = false
        _isLoading.value = false
        _data.value = moviesResponse
        _errorMessage.value = null
    }

    fun showUserCheckingForUpdatesFailure() {
        // No need to show error Message
        checkingForUpdates = false
        _isLoading.value = false
    }

    fun togglePowerMenuVisibility() {
        _isPowerMenuOpen.value = _isPowerMenuOpen.value == false
    }

    fun closePowerMenu() {
        _isPowerMenuOpen.value = false
    }

    fun showUserErrorScreen(errorMessage: String) {
        _isLoading.value = false
        _data.value = emptyList()
        _errorMessage.value = errorMessage
    }


    fun areFeaturedMoviesDisplayed() = !_data.value.isNullOrEmpty()
    fun getCurrentPowerMenuItem(): ToolbarCountryItems = _powerMenuItem.value!!
    fun getCurrentDisplayedMovies(): List<MoviesResponse> = _data.value!!
    fun isPowerMenuOpen(): Boolean = _isPowerMenuOpen.value!!
    fun isCheckingForUpdate(): Boolean = checkingForUpdates


}