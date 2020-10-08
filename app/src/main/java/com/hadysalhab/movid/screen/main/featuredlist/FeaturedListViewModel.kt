package com.hadysalhab.movid.screen.main.featuredlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.usecases.list.FetchFeaturedListUseCase
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import javax.inject.Inject

class FeaturedListViewModel @Inject constructor(
    private val fetchFeaturedListUseCase: FetchFeaturedListUseCase,
    private val moviesStateManager: MoviesStateManager,
    private val movieListScreenStateManager: MovieListScreenStateManager,
    private val dataValidator: DataValidator
) : ViewModel(), FetchFeaturedListUseCase.Listener {
    private var isFirstRender: Boolean = true

    private lateinit var groupType: GroupType
    private lateinit var region: String
    private lateinit var moviesResponse: MoviesResponse
    private val moviesList = mutableListOf<Movie>()

    private val emitter: EventEmitter<FeaturedListScreenEvents> = EventEmitter()
    val screenEvents: EventSource<FeaturedListScreenEvents> get() = emitter
    val state: LiveData<FeaturedListScreenState>
        get() = movieListScreenStateManager.stateLiveData

    init {
        fetchFeaturedListUseCase.registerListener(this)
    }

    fun onStart(groupType: GroupType, region: String) {
        if (isFirstRender) {
            movieListScreenStateManager.dispatch(FeaturedListActions.SetGroupType(groupType.value))
            isFirstRender = false
            this.groupType = groupType
            this.region = region
            val storedMoviesResponse =
                moviesStateManager.getMoviesResponseByGroupType(groupType)
            if (dataValidator.isMoviesResponseValid(
                    storedMoviesResponse,
                    region
                ) && storedMoviesResponse.movies != null
            ) {
                this.moviesResponse = storedMoviesResponse
                this.moviesList.addAll(storedMoviesResponse.movies)
                movieListScreenStateManager.dispatch(
                    FeaturedListActions.FeaturedListSuccess(
                        storedMoviesResponse.movies
                    )
                )
            } else {
                movieListScreenStateManager.dispatch(FeaturedListActions.FeaturedListRequest)
                fetchApi(1)
            }
        }
    }

    private fun fetchApi(page: Int) {
        fetchFeaturedListUseCase.fetchMoviesResponseUseCase(
            groupType = groupType,
            page = page,
            region = this.region
        )
    }

    //User Interactions-----------------------------------------------------------------------------

    fun loadMore() {
        if (fetchFeaturedListUseCase.isBusy || this.moviesResponse.page + 1 > this.moviesResponse.total_pages) {
            return
        }
        movieListScreenStateManager.dispatch(FeaturedListActions.Pagination)
        fetchApi(this.moviesResponse.page + 1)
    }

    //UseCaseResults--------------------------------------------------------------------------------

    override fun onFetchMoviesResponseSuccess(apiMoviesResponse: MoviesResponse) {
        this.moviesResponse = apiMoviesResponse
        moviesList.addAll(apiMoviesResponse.movies ?: emptyList())
        movieListScreenStateManager.dispatch(FeaturedListActions.FeaturedListSuccess(this.moviesList))
    }

    override fun onFetchMoviesResponseFailure(msg: String) {
        if (state.value!!.isPaginationLoading) {
            movieListScreenStateManager.dispatch(FeaturedListActions.FeaturedListPaginationError)
        } else {
            movieListScreenStateManager.dispatch(FeaturedListActions.FeaturedListError(msg))
        }
    }
    //----------------------------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        fetchFeaturedListUseCase.unregisterListener(this)
    }

}