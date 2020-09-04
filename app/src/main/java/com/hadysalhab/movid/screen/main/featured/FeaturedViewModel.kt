package com.hadysalhab.movid.screen.main.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.usecases.groups.FetchFeaturedMoviesUseCase
import javax.inject.Inject

class FeaturedViewModel @Inject constructor(
    private val fetchFeaturedMoviesUseCase: FetchFeaturedMoviesUseCase,
    private val moviesStateManager: MoviesStateManager,
    private val dataValidator: DataValidator,
    private val timeProvider: TimeProvider
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
                movieGroups = moviesStateManager.getFeaturedMovies()
                if (areMoviesValid()) {
                    _viewState.value = FeaturedLoaded(movieGroups)
                } else {
                    _viewState.value = Loading
                    fetchFeaturedMoviesUseCase.fetchFeaturedMoviesUseCaseAndNotify()
                }
            }
            Loading, is Error -> {
                return
            }
            is FeaturedLoaded -> {
                if (!areMoviesValid()) {
                    _viewState.value = Loading
                    fetchFeaturedMoviesUseCase.fetchFeaturedMoviesUseCaseAndNotify()
                }
            }
        }
    }

    private fun areMoviesValid(): Boolean {
        var result = true
        for (movieGroup in movieGroups) {
            if (!dataValidator.isMoviesResponseValid(movieGroup)) {
                result = false
                break
            }
        }
        return result
    }

    override fun onFetchMovieGroupsSucceeded(movieGroups: List<MoviesResponse>) {
        this.movieGroups = movieGroups
        this.movieGroups.forEach {
            it.timeStamp = timeProvider.currentTimestamp
        }
        moviesStateManager.updateAllMoviesResponse(movieGroups)
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