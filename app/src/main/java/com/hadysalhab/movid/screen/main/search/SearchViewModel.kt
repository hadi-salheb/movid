package com.hadysalhab.movid.screen.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.search.SearchMovieUseCase
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchMovieUseCase: SearchMovieUseCase
) : ViewModel(), SearchMovieUseCase.Listener {
    private val _viewState = MutableLiveData<SearchViewState>(Genres)
    private lateinit var moviesResponse: MoviesResponse
    private lateinit var query: String
    private val moviesList = mutableListOf<Movie>()
    val viewState: LiveData<SearchViewState>
        get() = _viewState

    init {
        searchMovieUseCase.registerListener(this)
    }

    fun searchMovie(text: CharSequence) {
        this.query = text.toString()
        if (searchMovieUseCase.isBusy) {
            return
        }
        moviesList.clear()
        _viewState.value = Loading
        searchMovieUseCase.searchMovieUseCase(this.query, 1)
    }

    fun loadMoreItems() {
        if (searchMovieUseCase.isBusy || this.moviesResponse.page + 1 > this.moviesResponse.total_pages) {
            return
        }
        _viewState.value = PaginationLoading
        searchMovieUseCase.searchMovieUseCase(
            page = this.moviesResponse.page + 1,
            query = this.query
        )
    }


    override fun onSearchMovieSuccess(movies: MoviesResponse, query: String) {
        if (_viewState.value != Genres) {
            if (query == this.query) {
                this.moviesResponse = movies
                moviesList.addAll(movies.movies ?: emptyList())
                _viewState.value = SearchLoaded(moviesList)
            } else {
                searchMovieUseCase.searchMovieUseCase(this.query, 1)
            }
        }
    }

    override fun onSearchMovieFailure(msg: String, query: String) {
        if (_viewState.value != Genres) {
            _viewState.value = Error(msg)
        }
    }

    fun setGenresState() {
        _viewState.value = Genres
    }

    override fun onCleared() {
        super.onCleared()
        searchMovieUseCase.unregisterListener(this)
    }
}