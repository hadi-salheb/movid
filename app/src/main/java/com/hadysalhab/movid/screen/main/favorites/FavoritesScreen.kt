package com.hadysalhab.movid.screen.main.favorites

import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class FavoritesScreenState(
    val isLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val data: List<Movie> = emptyList(),
    val error: String? = null
)

abstract class FavoritesScreen : BaseObservableViewMvc<FavoritesScreen.Listener>() {
    interface Listener {
        fun onMovieItemClicked(movieID: Int)
        fun loadMoreItems()
        fun onRetryClicked()
    }

    abstract fun handleScreenState(screenState: FavoritesScreenState)
    protected abstract fun showErrorScreen(errorMessage: String)
    protected abstract fun hideErrorScreen()
    protected abstract fun movieListScreenHandleState(movieListScreenState: MovieListScreenState)
    protected abstract fun setToolbarTitle(groupType: String)
}