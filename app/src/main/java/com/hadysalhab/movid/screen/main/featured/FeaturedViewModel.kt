package com.hadysalhab.movid.screen.main.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.usecases.groups.FetchFeaturedMoviesUseCase
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import javax.inject.Inject

class FeaturedViewModel @Inject constructor(
    private val fetchFeaturedMoviesUseCase: FetchFeaturedMoviesUseCase,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val moviesStateManager: MoviesStateManager,
    private val dataValidator: DataValidator,
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

    private var isFirstRender = true

    init {
        fetchFeaturedMoviesUseCase.registerListener(this)
        featuredScreenStateManager.updatePowerMenuItem(sharedPreferencesManager.getStoredFeaturedPowerMenuItem())
    }

    fun onStart() {
        if (isFirstRender) {
            isFirstRender = false
            val storeFeaturedMovies = getStoreFeaturedMovies()
            val areFeaturedMoviesValid = validateFeaturedMoviesAndReturnResult(storeFeaturedMovies)
            if (areFeaturedMoviesValid) {
                featuredScreenStateManager.showUserFeaturedMovies(storeFeaturedMovies)
            } else {
                featuredScreenStateManager.showUserLoadingScreen()
                fetchApiForFeaturedMovies()
            }
        } else if (areFeaturedMoviesDisplayed()) {
            val areDisplayedFeaturedMoviesValid =
                validateFeaturedMoviesAndReturnResult(featuredScreenStateManager.getCurrentDisplayedMovies())
            if (!areDisplayedFeaturedMoviesValid) {
                featuredScreenStateManager.showUserCheckingForUpdates()
                emitter.emit(ShowUserToastMessage("Please wait,checking for updates..."))
                fetchApiForFeaturedMovies()
            }
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
    //----------------------------------------------------------------------------------------------

    private fun areFeaturedMoviesDisplayed() =
        featuredScreenStateManager.areFeaturedMoviesDisplayed()

    private fun getStoreFeaturedMovies() = moviesStateManager.getFeaturedMovies()
    private fun validateFeaturedMoviesAndReturnResult(moviesResponse: List<MoviesResponse>) =
        dataValidator.areFeaturedMoviesValid(
            moviesResponse,
            powerMenuItem.value!!.region
        )


    private fun fetchApiForFeaturedMovies() {
        fetchFeaturedMoviesUseCase.fetchFeaturedMoviesUseCaseAndNotify(powerMenuItem.value!!.region)
    }


    override fun onFetchMovieGroupsSucceeded(movieGroups: List<MoviesResponse>) {
        if (featuredScreenStateManager.isCheckingForUpdate()) {
            featuredScreenStateManager.showUserCheckingForUpdatesSuccess(movieGroups)
            emitter.emit(ShowUserToastMessage("Movies Updated!..."))
        } else {
            featuredScreenStateManager.showUserFeaturedMovies(movieGroups)
        }
    }

    override fun onFetchMovieGroupsFailed(msg: String) {
        if (featuredScreenStateManager.isCheckingForUpdate()) {
            featuredScreenStateManager.showUserCheckingForUpdatesFailure()
            emitter.emit(ShowUserToastMessage("Updating Movies Failed!..."))
        } else {
            featuredScreenStateManager.showUserErrorScreen(msg)
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchFeaturedMoviesUseCase.unregisterListener(this)
    }
}