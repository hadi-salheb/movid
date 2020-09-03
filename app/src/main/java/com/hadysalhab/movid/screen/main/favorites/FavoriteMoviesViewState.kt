package com.hadysalhab.movid.screen.main.favorites

import com.hadysalhab.movid.movies.Movie

sealed class FavoriteMoviesViewState

object Loading : FavoriteMoviesViewState()

object PaginationLoading : FavoriteMoviesViewState()

data class Error(val errorMessage: String) : FavoriteMoviesViewState()

data class FavoriteMoviesLoaded(
    val movies: List<Movie>
) : FavoriteMoviesViewState()