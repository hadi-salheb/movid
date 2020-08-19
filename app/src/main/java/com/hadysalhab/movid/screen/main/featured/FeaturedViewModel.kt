package com.hadysalhab.movid.screen.main.featured

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.groups.FetchMovieGroupsUseCase
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import javax.inject.Inject

//Process-Death case is not handled, always try reload data in this case
class FeaturedViewModel @Inject constructor(
    private val fetchMovieGroupsUseCase: FetchMovieGroupsUseCase,
    private val deviceConfigManager: DeviceConfigManager
) : ViewModel(), FetchMovieGroupsUseCase.Listener {

    private val _viewState = MutableLiveData<FeaturedViewState>()
    val viewState: LiveData<FeaturedViewState>
        get() = _viewState

    private val emitter: EventEmitter<Events> = EventEmitter()
    val events: EventSource<Events> get() = emitter

    init {
        fetchMovieGroupsUseCase.registerListener(this)
    }

    fun onStart() {
        when (_viewState.value) {
            // re-fetch in case the data is outdated
            null,is FeaturedLoaded -> {
                fetchMovieGroupsUseCase.fetchMovieGroupsAndNotify(deviceConfigManager.getISO3166CountryCodeOrUS())
            }
            Loading, is Error -> {
                return
            }
        }
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