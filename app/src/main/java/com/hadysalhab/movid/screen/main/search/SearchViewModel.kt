package com.hadysalhab.movid.screen.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.search.SearchMovieUseCase
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchMovieUseCase: SearchMovieUseCase,
    private val searchScreenStateManager: SearchScreenStateManager
) : ViewModel(), SearchMovieUseCase.Listener {
    private lateinit var moviesResponse: MoviesResponse
    private val moviesList = mutableListOf<Movie>()
    private val dispatch = searchScreenStateManager::dispatch
    private lateinit var query: String
    val state: LiveData<SearchScreenState> =
        searchScreenStateManager.setInitialStateAndReturn(
            SearchScreenState(
                movieListScreenState = null
            )
        )
    private val stateValue: SearchScreenState
        get() = state.value!!


    //User Interactions-----------------------------------------------------------------------------
    fun loadMoreItems() {
        if (searchMovieUseCase.isBusy || this.moviesResponse.page + 1 > this.moviesResponse.total_pages) {
            return
        }
        dispatch(SearchScreenActions.Pagination)
        searchApi(this.moviesResponse.page + 1, this.query)
    }

    fun onSearchConfirmed(query: String) {
        if (searchMovieUseCase.isBusy || query.isEmpty()) {
            return
        }
        //reset
        moviesList.clear()
        moviesResponse = MoviesResponse(1, 0, 0, emptyList(), GroupType.SEARCH)
        this.query = query
        dispatch(SearchScreenActions.Request)
        searchMovieUseCase.registerListener(this)
        searchApi(1, query)
    }

    fun onSearchBackBtnClick() {
        searchMovieUseCase.unregisterListener(this)
        dispatch(SearchScreenActions.OnSearchClosed)
    }

    private fun searchApi(page: Int, query: String) {
        searchMovieUseCase.searchMovieUseCase(query, page)
    }

    fun onPaginationErrorClicked() {
        loadMoreItems()
    }

    fun onErrorRetryClicked() {
        dispatch(SearchScreenActions.Request)
        searchApi(1, query)
    }

    //UseCase Callbacks-----------------------------------------------------------------------------
    //Callbacks Will Only fire if the movieListState is diff than null...

    override fun onSearchMovieSuccess(movies: MoviesResponse, query: String) {
        if (query == this.query) {
            this.moviesResponse = movies
            moviesList.addAll(movies.movies ?: emptyList())
            dispatch(SearchScreenActions.Success(moviesList))
        } else {
            searchMovieUseCase.searchMovieUseCase(this.query, 1)
        }
    }

    override fun onSearchMovieFailure(msg: String, query: String) {
        if (query == this.query) {
            if (stateValue.movieListScreenState!!.paginationError) {
                dispatch(SearchScreenActions.PaginationError)
            } else {
                dispatch(SearchScreenActions.Error(msg))
            }
        } else {
            searchMovieUseCase.searchMovieUseCase(this.query, 1)
        }
    }

    //----------------------------------------------------------------------------------------------
    override fun onCleared() {
        super.onCleared()
        searchMovieUseCase.unregisterListener(this)
    }
}