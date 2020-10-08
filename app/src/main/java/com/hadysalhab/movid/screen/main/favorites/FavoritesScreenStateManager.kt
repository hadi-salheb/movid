package com.hadysalhab.movid.screen.main.favorites

import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.movies.Movie


sealed class FavoritesScreenActions {
    object FavoritesRequest : FavoritesScreenActions()
    data class FavoritesSuccess(val movies: List<Movie>) : FavoritesScreenActions()
    data class FavoritesError(val msg: String) : FavoritesScreenActions()
    object FavoritesPaginationError : FavoritesScreenActions()
    object Pagination : FavoritesScreenActions()
}

class FavoritesScreenStateManager {
    val stateLiveData = MutableLiveData<FavoritesScreenState>(FavoritesScreenState())
    private var state: FavoritesScreenState
        get() {
            return stateLiveData.value!!
        }
        set(value) {
            stateLiveData.value = value
        }

    fun dispatch(featuredListActions: FavoritesScreenActions) {
        when (featuredListActions) {
            is FavoritesScreenActions.FavoritesRequest -> {
                state = state.copy(
                    data = emptyList(),
                    isLoading = true,
                    error = null
                )
            }
            is FavoritesScreenActions.FavoritesSuccess -> {
                state = state.copy(
                    isLoading = false,
                    isPaginationLoading = false,
                    error = null,
                    data = featuredListActions.movies
                )
            }
            is FavoritesScreenActions.Pagination -> {
                state = state.copy(
                    isPaginationLoading = true
                )
            }
            is FavoritesScreenActions.FavoritesError -> {
                state = state.copy(
                    data = emptyList(),
                    isLoading = false,
                    error = featuredListActions.msg
                )
            }
            is FavoritesScreenActions.FavoritesPaginationError -> {
                state = state.copy(
                    isPaginationLoading = false
                )
            }
        }
    }
}