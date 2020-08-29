package com.hadysalhab.movid.screen.main.movielist

import com.hadysalhab.movid.movies.Movie

sealed class MovieListViewState

object Loading : MovieListViewState()

object PaginationLoading : MovieListViewState()

data class Error(val errorMessage: String) : MovieListViewState()

data class MovieListLoaded(
    val movies: List<Movie>
) : MovieListViewState()