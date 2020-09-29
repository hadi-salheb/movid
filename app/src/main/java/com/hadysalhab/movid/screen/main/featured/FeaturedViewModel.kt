package com.hadysalhab.movid.screen.main.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.groups.FetchFeaturedMoviesUseCase
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import javax.inject.Inject

class FeaturedViewModel @Inject constructor(
    private val fetchFeaturedMoviesUseCase: FetchFeaturedMoviesUseCase,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val featuredScreenStateManager: FeaturedScreenStateManager
) : ViewModel(), FetchFeaturedMoviesUseCase.Listener {

    private val emitter: EventEmitter<FeaturedEvents> = EventEmitter()
    val events: EventSource<FeaturedEvents> get() = emitter

    val isLoading: LiveData<Boolean>
        get() = featuredScreenStateManager.isLoading

    val isPowerMenuOpen: LiveData<Boolean>
        get() = featuredScreenStateManager.isPowerMenuOpen

    val errorMessage: LiveData<String?>
        get() = featuredScreenStateManager.errorMessage

    val data: LiveData<List<MoviesResponse>>
        get() = featuredScreenStateManager.data

    val powerMenuItem: LiveData<ToolbarCountryItems>
        get() = featuredScreenStateManager.powerMenuItem

    val refresh: LiveData<Boolean>
        get() = featuredScreenStateManager.isRefreshing

    private var isFirstRender = true

    init {
        fetchFeaturedMoviesUseCase.registerListener(this)
        featuredScreenStateManager.updatePowerMenuItem(sharedPreferencesManager.getStoredFeaturedPowerMenuItem())
    }

    fun onStart() {
        if (isFirstRender) {
            isFirstRender = false
            featuredScreenStateManager.showUserLoadingScreen()
            fetchApiForFeaturedMovies()
        }
    }

    //User Actions----------------------------------------------------------------------------------
    fun onOverflowMenuIconClick() {
        if (!fetchFeaturedMoviesUseCase.isBusy) {
            featuredScreenStateManager.togglePowerMenuVisibility()
        } else {
            emitter.emit(ShowUserToastMessage("Please Wait..."))
        }
    }

    fun onBackgroundClick() {
        featuredScreenStateManager.closePowerMenu()
    }

    fun onCountryToolbarItemClicked(toolbarCountryItem: ToolbarCountryItems) {
        sharedPreferencesManager.setStoredFeaturedPowerMenuItem(toolbarCountryItem)
        featuredScreenStateManager.showUserLoadingScreenWithNewPowerItem(toolbarCountryItem)
        fetchApiForFeaturedMovies()
    }

    fun onRetryClicked() {
        featuredScreenStateManager.showUserLoadingScreen()
        fetchApiForFeaturedMovies()
    }

    fun onRefresh() {
        featuredScreenStateManager.showUserRefresh()
        fetchApiForFeaturedMovies()
    }

    //----------------------------------------------------------------------------------------------
    private fun fetchApiForFeaturedMovies() {
        fetchFeaturedMoviesUseCase.fetchFeaturedMoviesUseCaseAndNotify(powerMenuItem.value!!.region)
    }


    override fun onFetchMovieGroupsSucceeded(movieGroups: List<MoviesResponse>) {
        if (featuredScreenStateManager.isRefreshing()) {
            featuredScreenStateManager.showUserRefreshSuccess(movieGroups)
            emitter.emit(ShowUserToastMessage("Movies Updated"))
        } else {
            featuredScreenStateManager.showUserFeaturedMovies(movieGroups)
        }
    }

    override fun onFetchMovieGroupsFailed(msg: String) {
        if (featuredScreenStateManager.isRefreshing()) {
            featuredScreenStateManager.showUserRefreshError()
            emitter.emit(ShowUserToastMessage("Updating Movies Failed"))
        } else {
            featuredScreenStateManager.showUserErrorScreen(msg)
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchFeaturedMoviesUseCase.unregisterListener(this)
    }
}