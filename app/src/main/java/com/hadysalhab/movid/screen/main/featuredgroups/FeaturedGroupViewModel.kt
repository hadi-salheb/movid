package com.hadysalhab.movid.screen.main.featuredgroups

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.groups.FetchFeaturedMoviesUseCase
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import javax.inject.Inject

class FeaturedGroupViewModel @Inject constructor(
    private val fetchFeaturedMoviesUseCase: FetchFeaturedMoviesUseCase,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val featuredScreenStateManager: FeaturedScreenStateManager
) : ViewModel(), FetchFeaturedMoviesUseCase.Listener {

    private val emitter: EventEmitter<FeaturedScreenEvents> = EventEmitter()
    val screenEvents: EventSource<FeaturedScreenEvents> get() = emitter

    val state: LiveData<FeaturedScreenState> = featuredScreenStateManager.stateLiveData
    private var isFirstRender: Boolean = true

    init {
        fetchFeaturedMoviesUseCase.registerListener(this)
    }

    fun onStart() {
        if (isFirstRender) {
            isFirstRender = false
            featuredScreenStateManager.dispatch(
                FeaturedActions.FeaturedRequest(
                    sharedPreferencesManager.getStoredFeaturedPowerMenuItem()
                )
            )
            fetchApiForFeaturedMovies()
        }
    }

    //User Actions----------------------------------------------------------------------------------
    fun onOverflowMenuIconClick() {
        if (!fetchFeaturedMoviesUseCase.isBusy) {
            featuredScreenStateManager.dispatch(FeaturedActions.TogglePowerMenuVisibility)
        } else {
            emitter.emit(ShowUserToastMessage("Please Wait..."))
        }
    }

    fun onBackgroundClick() {
        featuredScreenStateManager.dispatch(FeaturedActions.ClosePowerMenu)
    }

    fun onCountryToolbarItemClicked(toolbarCountryItem: ToolbarCountryItems) {
        sharedPreferencesManager.setStoredFeaturedPowerMenuItem(toolbarCountryItem)
        featuredScreenStateManager.dispatch(FeaturedActions.FeaturedRequest(toolbarCountryItem))
        fetchApiForFeaturedMovies()
    }

    fun onRetryClicked() {
        if (fetchFeaturedMoviesUseCase.isBusy) {
            return
        }
        featuredScreenStateManager.dispatch(FeaturedActions.FeaturedRequest(state.value!!.powerMenuItem))
        fetchApiForFeaturedMovies()
    }

    fun onRefresh() {
        if (fetchFeaturedMoviesUseCase.isBusy) {
            return
        }
        featuredScreenStateManager.dispatch(FeaturedActions.FeaturedRefresh)
        fetchApiForFeaturedMovies()
    }

    //----------------------------------------------------------------------------------------------
    private fun fetchApiForFeaturedMovies() {
        fetchFeaturedMoviesUseCase.fetchFeaturedMoviesUseCaseAndNotify(state.value!!.powerMenuItem.region)
    }


    override fun onFetchMovieGroupsSucceeded(movieGroups: List<MoviesResponse>) {
        if (state.value!!.isRefreshing) {
            featuredScreenStateManager.dispatch(FeaturedActions.FeaturedSuccess(movieGroups))
            emitter.emit(ShowUserToastMessage("Movies Updated"))
        } else {
            featuredScreenStateManager.dispatch(FeaturedActions.FeaturedSuccess(movieGroups))
        }
    }

    override fun onFetchMovieGroupsFailed(msg: String) {
        if (state.value!!.isRefreshing) {
            featuredScreenStateManager.dispatch(FeaturedActions.FeaturedRefreshError)
            emitter.emit(ShowUserToastMessage(msg))
        } else {
            featuredScreenStateManager.dispatch(FeaturedActions.FeaturedError(msg))
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchFeaturedMoviesUseCase.unregisterListener(this)
    }
}