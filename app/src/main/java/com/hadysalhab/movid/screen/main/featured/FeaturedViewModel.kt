package com.hadysalhab.movid.screen.main.featured

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.groups.FetchFeaturedMoviesUseCase
import javax.inject.Inject

//Process-Death case is not handled, always try reload data in this case
class FeaturedViewModel @Inject constructor(
    private val fetchFeaturedMoviesUseCase: FetchFeaturedMoviesUseCase,
    private val dataValidator: DataValidator
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
            null->{
                fetchFeaturedMoviesUseCase.fetchFeaturedMoviesUseCase()
            }
            Loading, is Error -> {
                return
            }
            is FeaturedLoaded ->{
                if(!areMoviesStillValid()){
                    fetchFeaturedMoviesUseCase.fetchFeaturedMoviesUseCase()
                }
            }
        }
    }
    private fun areMoviesStillValid():Boolean{
        var result = true
        for(movieGroup in movieGroups){
            if(!dataValidator.isMoviesResponseValid(movieGroup)){
                result = false
                break
            }
        }
        return result
    }

    override fun onFetchMovieGroupsSucceeded(movieGroups: List<MoviesResponse>) {
        this.movieGroups = movieGroups
        val featuredLoaded = FeaturedLoaded(movieGroups)
        _viewState.value = featuredLoaded
    }

    override fun onFetchMovieGroupsFailed(msg: String) {
        val error = Error(msg)
        _viewState.value = error
    }

    override fun onFetchMovieGroups() {
        _viewState.value = Loading
    }

    override fun onCleared() {
        super.onCleared()
        fetchFeaturedMoviesUseCase.unregisterListener(this)
    }

}