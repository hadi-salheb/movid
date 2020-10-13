package com.hadysalhab.movid.screen.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState


sealed class SearchScreenActions {
    object Request : SearchScreenActions()
    data class Success(val movies: List<Movie>) : SearchScreenActions()
    data class Error(val msg: String) : SearchScreenActions()
    object PaginationError : SearchScreenActions()
    object Pagination : SearchScreenActions()
    object OnSearchClosed : SearchScreenActions()
}

class SearchScreenStateManager {
    private val stateLiveData = MutableLiveData<SearchScreenState>()
    private var state: SearchScreenState
        get() {
            return stateLiveData.value!!
        }
        set(value) {
            stateLiveData.value = value
        }

    fun setInitialStateAndReturn(initialState: SearchScreenState): LiveData<SearchScreenState> {
        stateLiveData.value = initialState
        return stateLiveData
    }

    fun dispatch(action: SearchScreenActions) {
        state = searchScreenReducer(action)
    }

    private fun searchScreenReducer(action: SearchScreenActions): SearchScreenState =
        when (action) {
            is SearchScreenActions.OnSearchClosed -> {
                state.copy(movieListScreenState = null)
            }
            is SearchScreenActions.Request -> {
                state.copy(
                    movieListScreenState = MovieListScreenState(
                        data = emptyList(),
                        isLoading = true,
                        errorMessage = null,
                        emptyResultsIconDrawable = R.drawable.ic_sad,
                        emptyResultsMessage = "No Results Found"
                    )
                )
            }
            is SearchScreenActions.Success -> {
                if (state.movieListScreenState != null) {
                    val movies = mutableListOf<Movie>()
                    movies.addAll(action.movies)
                    state.copy(
                        movieListScreenState = state.movieListScreenState!!.copy(
                            isLoading = false,
                            isPaginationLoading = false,
                            errorMessage = null,
                            data = movies
                        )
                    )
                } else {
                    state
                }
            }
            is SearchScreenActions.Pagination -> {
                if (state.movieListScreenState != null) {
                    state.copy(
                        movieListScreenState = state.movieListScreenState!!.copy(
                            isPaginationLoading = true,
                            paginationError = false
                        )
                    )
                } else {
                    state
                }
            }
            is SearchScreenActions.Error -> {
                if (state.movieListScreenState != null) {
                    state.copy(
                        movieListScreenState = state.movieListScreenState!!.copy(
                            data = emptyList(),
                            isLoading = false,
                            errorMessage = action.msg
                        )
                    )
                } else {
                    state
                }
            }
            is SearchScreenActions.PaginationError -> {
                if (state.movieListScreenState != null) {
                    state.copy(
                        movieListScreenState = state.movieListScreenState!!.copy(
                            isPaginationLoading = false,
                            paginationError = true
                        )
                    )
                } else {
                    state
                }
            }
        }
}