package com.hadysalhab.movid.screen.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.search.SearchMovieUseCase
import com.hadysalhab.movid.screen.common.viewmodels.SavedStateViewModel
import javax.inject.Inject

private const val SEARCH_QUERY_KEY = "com.hadysalhab.movid.screen.main.search.key"

class SearchViewModel @Inject constructor(
    private val searchMovieUseCase: SearchMovieUseCase,
    private val searchScreenStateManager: SearchScreenStateManager
) : SavedStateViewModel(), SearchMovieUseCase.Listener {
    private var isFirstRender = true
    private lateinit var moviesResponse: MoviesResponse
    private val moviesList = mutableListOf<Movie>()
    private val dispatch = searchScreenStateManager::dispatch
    private lateinit var query: String
    private lateinit var savedStateHandle: SavedStateHandle
    val state: LiveData<SearchScreenState> =
        searchScreenStateManager.setInitialStateAndReturn(
            SearchScreenState(
                movieListScreenState = null
            )
        )
    private val stateValue: SearchScreenState
        get() = state.value!!


    override fun init(savedStateHandle: SavedStateHandle) {
        this.savedStateHandle = savedStateHandle
        query = savedStateHandle.get<String>(SEARCH_QUERY_KEY) ?: ""
    }


    fun onStart() {
        if (isFirstRender) {
            isFirstRender = false
            if (this.query.isEmpty()) {
                //do nothing. already movieListScreenState is null
            } else {
                onSearchConfirmed(this.query)
            }
        }

    }


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
        reset()
        updateQuery(query)
        dispatch(SearchScreenActions.Request)
        searchMovieUseCase.registerListener(this)
        searchApi(1, query)
    }

    fun onSearchBackBtnClick() {
        reset()
        updateQuery("")
        searchMovieUseCase.unregisterListener(this)
        dispatch(SearchScreenActions.OnSearchClosed)
    }

    private fun searchApi(page: Int, query: String) {
        searchMovieUseCase.searchMovieUseCase(query, page)
    }

    private fun reset() {
        moviesList.clear()
        moviesResponse = MoviesResponse(1, 0, 0, emptyList(), GroupType.SEARCH)
    }

    private fun updateQuery(query: String) {
        this.query = query
        savedStateHandle.set(SEARCH_QUERY_KEY, this.query)
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