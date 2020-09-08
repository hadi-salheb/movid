package com.hadysalhab.movid.screen.main.watchlist

import com.hadysalhab.movid.movies.Movie

sealed class WishlistMoviesViewState

object Loading : WishlistMoviesViewState()

object PaginationLoading : WishlistMoviesViewState()

data class Error(val errorMessage: String) : WishlistMoviesViewState()

data class WatchlistMoviesLoaded(
    val movies: List<Movie>
) : WishlistMoviesViewState()