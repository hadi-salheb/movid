package com.hadysalhab.movid.screen.main.moviedetail

import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.movies.MovieDetail


sealed class MovieDetailActions {
    object Request : MovieDetailActions()
    object Refresh : MovieDetailActions()
    data class Success(val data: MovieDetail) : MovieDetailActions()
    object RefreshError : MovieDetailActions()
    object WatchlistFavoriteError : MovieDetailActions()
    data class Error(val errorMessage: String) : MovieDetailActions()
    object Reset : MovieDetailActions()
}

class MovieDetailScreenStateManager {

    val stateLiveData = MutableLiveData(MovieDetailScreenState())
    private var state: MovieDetailScreenState
        get() {
            return stateLiveData.value!!
        }
        set(value) {
            stateLiveData.value = value
        }

    fun dispatch(movieDetailActions: MovieDetailActions) {
        when (movieDetailActions) {
            is MovieDetailActions.Request -> {
                state = state.copy(
                    isLoading = true,
                    isRefreshing = false,
                    error = null
                )
            }
            is MovieDetailActions.Refresh -> {
                state = state.copy(
                    isLoading = false,
                    isRefreshing = true,
                    error = null
                )
            }
            is MovieDetailActions.Success -> {
                state = state.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = null,
                    data = movieDetailActions.data
                )
            }

            is MovieDetailActions.Error -> {
                state = state.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = movieDetailActions.errorMessage
                )
            }
            is MovieDetailActions.Reset -> {
                state = MovieDetailScreenState()
            }
            is MovieDetailActions.RefreshError -> {
                state = state.copy(
                    isRefreshing = false
                )
            }
            is MovieDetailActions.WatchlistFavoriteError -> {
                state = state.copy(
                    isLoading = false
                )
            }

        }
    }

}
