package com.hadysalhab.movid.screen.main.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.groups.FetchFeaturedMoviesUseCase
import javax.inject.Inject

class FeaturedViewModel @Inject constructor(
    private val fetchFeaturedMoviesUseCase: FetchFeaturedMoviesUseCase
) : ViewModel(), FetchFeaturedMoviesUseCase.Listener {
    private lateinit var movieGroups: List<MoviesResponse>
    private val _viewState = MutableLiveData<FeaturedViewState>()
    val viewState: LiveData<FeaturedViewState>
        get() = _viewState

    init {
        fetchFeaturedMoviesUseCase.registerListener(this)
    }

    fun onStart() {
        when (_viewState.value) {
            null -> {
                _viewState.value = Loading
                fetchFeaturedMoviesUseCase.fetchFeaturedMoviesUseCaseAndNotify()
            }
            Loading, is Error -> {
                return
            }
            is FeaturedLoaded -> {
                fetchFeaturedMoviesUseCase.fetchFeaturedMoviesUseCaseAndNotify()
            }
        }
    }

    override fun onFetchMovieGroupsSucceeded(apiMovieGroups: List<MoviesResponse>) {
        this.movieGroups = apiMovieGroups
        val featuredLoaded = FeaturedLoaded(movieGroups)
        _viewState.value = featuredLoaded
    }

    override fun onFetchMovieGroupsFailed(msg: String) {
        val error = Error(msg)
        _viewState.value = error
    }

    override fun onCleared() {
        super.onCleared()
        fetchFeaturedMoviesUseCase.unregisterListener(this)
    }

}