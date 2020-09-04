package com.hadysalhab.movid.screen.main.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.favorites.FetchFavoriteMoviesUseCase
import javax.inject.Inject

class FavoriteMoviesViewModel @Inject constructor(
    private val fetchFavoriteMoviesUseCase: FetchFavoriteMoviesUseCase
) : ViewModel(), FetchFavoriteMoviesUseCase.Listener {
    private val _viewState = MutableLiveData<FavoriteMoviesViewState>()
    private var page = 1
    private var totalPages = 1
    private val moviesList = mutableListOf<Movie>()
    val viewState: LiveData<FavoriteMoviesViewState>
        get() = _viewState

    init {
        fetchFavoriteMoviesUseCase.registerListener(this)
        fetchFavoriteMoviesUseCase.fetchFavoriteMoviesUseCase(
            page = page
        )
    }

    fun loadMore() {
        if (fetchFavoriteMoviesUseCase.isBusy || this.page + 1 > this.totalPages) {
            return
        }
        fetchFavoriteMoviesUseCase.fetchFavoriteMoviesUseCase(
            page = ++page
        )
    }

    override fun onFetchFavoriteMoviesSuccess(movies: MoviesResponse) {
        this.totalPages = movies.total_pages
        moviesList.addAll(movies.movies ?: emptyList())
        _viewState.value = FavoriteMoviesLoaded(moviesList)
    }

    override fun onFetchFavoriteMoviesFailure(msg: String) {
        _viewState.value = Error(msg)
    }

    override fun onCleared() {
        super.onCleared()
        fetchFavoriteMoviesUseCase.unregisterListener(this)
    }

}