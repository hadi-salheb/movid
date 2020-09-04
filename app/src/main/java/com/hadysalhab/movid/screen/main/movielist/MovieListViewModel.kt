package com.hadysalhab.movid.screen.main.movielist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.usecases.list.FetchMoviesResponseUseCase
import javax.inject.Inject

class MovieListViewModel @Inject constructor(
    private val fetchMoviesResponseUseCase: FetchMoviesResponseUseCase,
    private val moviesStateManager: MoviesStateManager,
    private val dataValidator: DataValidator,
    private val timeProvider: TimeProvider
) : ViewModel(), FetchMoviesResponseUseCase.Listener {
    private val _viewState = MutableLiveData<MovieListViewState>()
    private lateinit var groupType: GroupType
    private var movieID: Int? = null
    private lateinit var moviesResponse: MoviesResponse
    private var pageInRequest = 0
    private var pageDisplayed = 0
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
                val moviesResponseStore = moviesStateManager.getMoviesResponseByGroupType(groupType)
                if (dataValidator.isMoviesResponseValid(moviesResponseStore)) {
                    this.moviesResponse = moviesResponseStore
                    this.moviesList.apply {
                        clear()
                        addAll(moviesResponseStore.movies ?: emptyList())
                    }
                    _viewState.value = MovieListLoaded(this.moviesList)
                    this.pageDisplayed++
                } else {
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
    }

    fun loadMore() {
        if (fetchMoviesResponseUseCase.isBusy || this.pageDisplayed + 1 > this.moviesResponse.total_pages) {
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

    override fun onFetchMoviesResponseSuccess(moviesResponse: MoviesResponse) {
        if (!this::moviesResponse.isInitialized) {
            this.moviesResponse = moviesResponse
            this.moviesResponse.timeStamp = timeProvider.currentTimestamp
            moviesStateManager.updateMoviesResponse(this.moviesResponse)
        }
        moviesList.addAll(moviesResponse.movies ?: emptyList())
        _viewState.value = MovieListLoaded(moviesList)
        this.pageDisplayed++
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