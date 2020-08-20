package com.hadysalhab.movid.screen.main.movielist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.list.FetchMovieListUseCase
import javax.inject.Inject

class MovieListViewModel @Inject constructor(
    private val deviceConfigManager: DeviceConfigManager,
    private val fetchMovieListUseCase: FetchMovieListUseCase
) : ViewModel(), FetchMovieListUseCase.Listener {
    private val _viewState = MutableLiveData<MovieListViewState>()
    private var page: Int
    private lateinit var groupType: String
    private var totalPages: Int? = null
    private val movies: MutableList<Movie> = mutableListOf()
    private val region = deviceConfigManager.getISO3166CountryCodeOrUS()
    val viewState: LiveData<MovieListViewState>
        get() = _viewState

    init {
        fetchMovieListUseCase.registerListener(this)
        page = 1
    }

    fun init(groupType: String) {
        if (!this::groupType.isInitialized) {
            this.groupType = groupType
        }
        when (viewState.value) {
            null -> {
                fetchMovieListUseCase.fetchMovieListAndNotify(
                    region,
                    groupType, page
                )
            }
        }

    }

    fun loadMore() {
        if (fetchMovieListUseCase.isBusy) {
            return
        }
        if (page + 1 <= totalPages!!) {
            page++
            fetchMovieListUseCase.fetchMovieListAndNotify(
                region,
                this.groupType,
                page
            )
        }
    }


    override fun onFetchingMovieList() {
        if (page == 1) {
            _viewState.value = Loading
        } else {
            _viewState.value = PaginationLoading
        }
    }

    override fun onFetchMovieListSuccess(moviesResponse: MoviesResponse) {
        if (totalPages == null) {
            totalPages = moviesResponse.total_pages
        }
        movies.addAll(moviesResponse.movies ?: emptyList())
        _viewState.value = MovieListLoaded(movies)
    }

    override fun onFetchMovieListFailed(msg: String) {
        _viewState.value = Error(msg)
    }

    override fun onCleared() {
        super.onCleared()
        fetchMovieListUseCase.unregisterListener(this)
    }
}