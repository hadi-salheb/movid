package com.hadysalhab.movid.screen.common.listtitletoolbar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.movies.Movie


sealed class ListWithToolbarTitleActions {
    data class SetTitle(val movieName: String) : ListWithToolbarTitleActions()
    object Request : ListWithToolbarTitleActions()
    data class Success(val movies: List<Movie>) : ListWithToolbarTitleActions()
    data class Error(val msg: String) : ListWithToolbarTitleActions()
    object PaginationError : ListWithToolbarTitleActions()
    object Pagination : ListWithToolbarTitleActions()
}


class ListWithToolbarTitleStateManager {
    private val stateLiveData = MutableLiveData<ListWithToolbarTitleState>()

    fun setInitialStateAndReturn(initialState: ListWithToolbarTitleState): LiveData<ListWithToolbarTitleState> {
        stateLiveData.value = initialState
        return stateLiveData
    }

    private var state: ListWithToolbarTitleState
        get() {
            return stateLiveData.value!!
        }
        set(value) {
            stateLiveData.value = value
        }

    fun dispatch(listWithToolbarTitleActions: ListWithToolbarTitleActions) {
        state = listWithToolbarTitleReducer(listWithToolbarTitleActions)
    }

    private fun listWithToolbarTitleReducer(listWithToolbarTitleActions: ListWithToolbarTitleActions) =
        when (listWithToolbarTitleActions) {
            is ListWithToolbarTitleActions.SetTitle -> {
                state.copy(
                    title = listWithToolbarTitleActions.movieName
                )
            }
            is ListWithToolbarTitleActions.Request -> {
                state.copy(
                    data = emptyList(),
                    isLoading = true,
                    error = null
                )
            }
            is ListWithToolbarTitleActions.Success -> {
                val movies = mutableListOf<Movie>()
                movies.addAll(listWithToolbarTitleActions.movies)
                state.copy(
                    isLoading = false,
                    isPaginationLoading = false,
                    error = null,
                    data = movies
                )
            }
            is ListWithToolbarTitleActions.Pagination -> {
                state.copy(
                    isPaginationLoading = true,
                    isPaginationError = false
                )
            }
            is ListWithToolbarTitleActions.Error -> {
                state.copy(
                    data = emptyList(),
                    isLoading = false,
                    error = listWithToolbarTitleActions.msg
                )
            }
            is ListWithToolbarTitleActions.PaginationError -> {
                state.copy(
                    isPaginationLoading = false,
                    isPaginationError = true
                )
            }
        }
}