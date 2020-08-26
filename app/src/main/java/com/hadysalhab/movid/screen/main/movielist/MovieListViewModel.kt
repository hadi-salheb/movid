package com.hadysalhab.movid.screen.main.movielist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.usecases.list.FetchMovieListUseCase
import com.hadysalhab.movid.movies.usecases.list.FetchMovieListUseCaseFactory
import javax.inject.Inject

class MovieListViewModel @Inject constructor(
    deviceConfigManager: DeviceConfigManager,
    private val fetchMovieListUseCaseFactory: FetchMovieListUseCaseFactory
) : ViewModel(), FetchMovieListUseCase.Listener {
    private val _viewState = MutableLiveData<MovieListViewState>()
    private lateinit var groupType: GroupType
    private lateinit var fetchMovieListUseCase: FetchMovieListUseCase
    private var movieID: Int? = null
    private var page = 1
    private val region = deviceConfigManager.getISO3166CountryCodeOrUS()
    val viewState: LiveData<MovieListViewState>
        get() = _viewState


    fun init(groupType: GroupType, movieID: Int?) {
        if (!this::groupType.isInitialized) {
            this.groupType = groupType
            this.movieID = movieID
            this.fetchMovieListUseCase =
                fetchMovieListUseCaseFactory.makeFetchListUseCase(groupType)
            fetchMovieListUseCase.registerListener(this)
        }
        when (viewState.value) {
            null -> {
                fetchMovieListUseCase.fetchMovieListUseCase(
                    region,
                    page,
                    movieID
                )
            }
        }
    }

    fun loadMore() {
        if (fetchMovieListUseCase.isBusy) {
            return
        }
        fetchMovieListUseCase.fetchMovieListUseCase(
            region,
            ++page,
            movieID
        )
    }

    override fun onCleared() {
        super.onCleared()
        fetchMovieListUseCase.unregisterListener(this)
    }

    override fun onFetching() {
        if (page == 1) {
            _viewState.value = Loading
        } else {
            _viewState.value = PaginationLoading
        }
    }

    override fun onFetchSuccess(movies: List<Movie>) {
        _viewState.value = MovieListLoaded(movies)
    }

    override fun onFetchError(err: String) {
        _viewState.value = Error(err)
    }
}