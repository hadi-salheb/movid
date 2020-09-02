package com.hadysalhab.movid.screen.main.movielist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.list.FetchMoviesResponseUseCase
import javax.inject.Inject

class MovieListViewModel @Inject constructor(
    private val fetchMoviesResponseUseCase: FetchMoviesResponseUseCase
) : ViewModel(), FetchMoviesResponseUseCase.Listener {
    private val _viewState = MutableLiveData<MovieListViewState>()
    private lateinit var groupType: GroupType
    private var movieID: Int? = null
    private var page = 1
    private var totalPages = 1
    private val moviesList = mutableListOf<Movie>()

    val viewState: LiveData<MovieListViewState>
        get() = _viewState


    fun init(groupType: GroupType, movieID: Int?) {
        if (!this::groupType.isInitialized) {
            this.groupType = groupType
            this.movieID = movieID
            fetchMoviesResponseUseCase.registerListener(this)
        }
        when (viewState.value) {
            null -> {
                fetchMoviesResponseUseCase.fetchMoviesResponseUseCase(
                    groupType = groupType,
                    page = page,
                    movieId = movieID
                )
            }
        }
    }

    fun loadMore() {
        if (fetchMoviesResponseUseCase.isBusy || this.page + 1 > this.totalPages) {
            return
        }
        fetchMoviesResponseUseCase.fetchMoviesResponseUseCase(
            groupType = groupType,
            page = ++page,
            movieId = movieID
        )
    }

    override fun onFetchMoviesResponseSuccess(moviesResponse: MoviesResponse) {
        this.totalPages = moviesResponse.total_pages
        moviesList.addAll(moviesResponse.movies ?: emptyList())
        _viewState.value = MovieListLoaded(moviesList)
    }

    override fun onFetchMoviesResponseFailure(msg: String) {
        _viewState.value = Error(msg)
    }

    override fun onFetchMoviesResponse() {
        if (page == 1) {
            _viewState.value = Loading
        } else {
            _viewState.value = PaginationLoading
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchMoviesResponseUseCase.unregisterListener(this)
    }

}