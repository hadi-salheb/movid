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

    protected abstract fun hideLoadingIndicator()
    protected abstract fun showLoadingIndicator()
    protected abstract fun showErrorScreen(msg: String)
    protected abstract fun hideErrorScreen()
    protected abstract fun showEmptyDataScreen(@DrawableRes icon: Int, msg: String)
    protected abstract fun hideEmptyDataScreen()
    protected abstract fun showPagination(data: List<Movie>)
    protected abstract fun showPaginationError(data: List<Movie>)
    protected abstract fun showData(data: List<Movie>)
    abstract fun handleState(movieListScreenState: MovieListScreenState)
}