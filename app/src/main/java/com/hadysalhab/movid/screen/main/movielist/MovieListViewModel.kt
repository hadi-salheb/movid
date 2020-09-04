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
    private lateinit var moviesResponse: MoviesResponse
    private var pageInRequest = 0
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
                _viewState.value = Loading
                pageInRequest = 1
                fetchMoviesResponseUseCase.fetchMoviesResponseUseCase(
                    groupType = groupType,
                    page = pageInRequest,
                    movieId = movieID
                )
            }
        }
    }

    fun loadMore() {
        if (fetchMoviesResponseUseCase.isBusy || this.moviesResponse.page + 1 > this.moviesResponse.total_pages) {
            return
        }
        _viewState.value = PaginationLoading
        this.pageInRequest++
        fetchMoviesResponseUseCase.fetchMoviesResponseUseCase(
            groupType = groupType,
            page = pageInRequest,
            movieId = movieID
        )
    }

    //UseCaseResults--------------------------------------------------------------------------------

    override fun onFetchMoviesResponseSuccess(apiMoviesResponse: MoviesResponse) {
        this.moviesResponse = apiMoviesResponse
        moviesList.addAll(apiMoviesResponse.movies ?: emptyList())
        _viewState.value = MovieListLoaded(moviesList)
    }

    override fun onFetchMoviesResponseFailure(msg: String) {
        _viewState.value = Error(msg)
    }
    //----------------------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        fetchMoviesResponseUseCase.unregisterListener(this)
    }

}