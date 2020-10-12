package com.hadysalhab.movid.screen.common.movielist

import androidx.annotation.DrawableRes
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class MovieListScreenState(
    val isLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val data: List<Movie>,
    @DrawableRes val emptyResultsIconDrawable: Int,
    val emptyResultsMessage: String,
    val errorMessage: String?,
    val paginationError: Boolean = false
)

abstract class MovieListScreen : BaseObservableViewMvc<MovieListScreen.Listener>() {
    interface Listener {
        fun onMovieItemClicked(movieID: Int)
        fun loadMoreItems()
        fun onRetryClicked()
        fun onPaginationErrorClicked()
    }

    abstract fun displayMovies(movies: List<Movie>)
    abstract fun showPaginationIndicator()
    abstract fun hidePaginationIndicator()
    abstract fun showLoadingIndicator()
    abstract fun hideLoadingIndicator()
    abstract fun handleState(movieListScreenState: MovieListScreenState)
}