package com.hadysalhab.movid.screen.main.featuredlist

import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.movielist.MovieListScreenState
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class FeaturedListScreenState(
    val isLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val data: List<Movie> = emptyList(),
    val error: String? = null,
    val groupType: String = ""
)

abstract class FeaturedListScreen : BaseObservableViewMvc<FeaturedListScreen.Listener>() {
    interface Listener {
        fun onMovieItemClicked(movieID: Int)
        fun loadMoreItems()
    }

    abstract fun handleScreenState(screenState: FeaturedListScreenState)
    protected abstract fun showErrorScreen(errorMessage: String)
    protected abstract fun hideErrorScreen()
    protected abstract fun movieListScreenHandleState(movieListScreenState: MovieListScreenState)
    protected abstract fun displayGroupType(groupType: String)
}