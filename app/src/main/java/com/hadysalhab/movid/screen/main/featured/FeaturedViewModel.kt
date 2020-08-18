package com.hadysalhab.movid.screen.main.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.groups.FetchMovieGroupsUseCase
import javax.inject.Inject

class FeaturedViewModel @Inject constructor(
    private val fetchMovieGroupsUseCase: FetchMovieGroupsUseCase,
    private val deviceConfigManager: DeviceConfigManager
) : ViewModel(), FetchMovieGroupsUseCase.Listener {

    private val _viewState = MutableLiveData<FeaturedViewState>()
    val viewState: LiveData<FeaturedViewState>
        get() = _viewState

    init {
        _viewState.value = Loading
        fetchMovieGroupsUseCase.registerListener(this)
        fetchMovieGroupsUseCase.fetchMovieGroupsAndNotify(deviceConfigManager.getISO3166CountryCodeOrUS())
    }

    override fun onFetchMovieGroupsSucceeded(movieGroups: List<MoviesResponse>) {
        val featuredLoaded = FeaturedLoaded(movieGroups)
        _viewState.value = featuredLoaded
    }

    override fun onFetchMovieGroupsFailed(msg: String) {
        val error = Error(msg)
        _viewState.value = error
    }

    override fun onFetching() {
        _viewState.value = Loading
    }

    override fun onCleared() {
        super.onCleared()
        fetchMovieGroupsUseCase.unregisterListener(this)
    }

}