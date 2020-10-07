package com.hadysalhab.movid.screen.main.moviedetail

import androidx.lifecycle.MutableLiveData
import com.hadysalhab.movid.movies.MovieDetail


data class MovieDetailScreenState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val data: MovieDetail? = null,
    val error: String? = null
)

sealed class MovieDetailActions {
    object MovieDetailRequest : MovieDetailActions()
    object MovieDetailRefresh : MovieDetailActions()
    data class MovieDetailSuccess(val data: MovieDetail) : MovieDetailActions()
    object MovieDetailRefreshError : MovieDetailActions()
    object MovieDetailFavoriteWatchlistError : MovieDetailActions()
    data class MovieDetailError(val errorMessage: String) : MovieDetailActions()
    object Reset : MovieDetailActions()
}

class MovieDetailScreenStateManager {

    val stateLiveData = MutableLiveData<MovieDetailScreenState>(MovieDetailScreenState())
    private var state: MovieDetailScreenState
        get() {
            return stateLiveData.value!!
        }
        set(value) {
            stateLiveData.value = value
        }

    fun dispatch(movieDetailActions: MovieDetailActions) {
        when (movieDetailActions) {
            is MovieDetailActions.MovieDetailRequest -> {
                state = state.copy(
                    isLoading = true,
                    isRefreshing = false,
                    error = null
                )
            }
            is MovieDetailActions.MovieDetailRefresh -> {
                state = state.copy(
                    isLoading = false,
                    isRefreshing = true,
                    error = null
                )
            }
            is MovieDetailActions.MovieDetailSuccess -> {
                state = state.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = null,
                    data = movieDetailActions.data
                )
            }

            is MovieDetailActions.MovieDetailError -> {
                state = state.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = movieDetailActions.errorMessage
                )
            }
            is MovieDetailActions.Reset -> {
                state = MovieDetailScreenState()
            }
            is MovieDetailActions.MovieDetailRefreshError -> {
                state = state.copy(
                    isRefreshing = false
                )
            }
            is MovieDetailActions.MovieDetailFavoriteWatchlistError -> {
                state = state.copy(
                    isLoading = false
                )
            }

        }
    }

}
