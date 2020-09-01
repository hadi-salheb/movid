package com.hadysalhab.movid.screen.main.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.groups.FetchMovieGroupsUseCase
import javax.inject.Inject

//Process-Death case is not handled, always try reload data in this case
class FeaturedViewModel @Inject constructor(
    private val fetchMovieGroupsUseCase: FetchMovieGroupsUseCase
) : ViewModel(), FetchMovieGroupsUseCase.Listener {

    private val _viewState = MutableLiveData<FeaturedViewState>()
    val viewState: LiveData<FeaturedViewState>
        get() = _viewState

    init {
        fetchMovieGroupsUseCase.registerListener(this)
    }

    fun onStart() {
        when (_viewState.value) {
            // re-fetch in case the data is outdated
            null, is FeaturedLoaded -> {
                fetchMovieGroupsUseCase.fetchMovieGroupsAndNotify()
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