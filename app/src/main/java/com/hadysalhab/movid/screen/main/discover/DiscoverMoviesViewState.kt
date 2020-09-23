package com.hadysalhab.movid.screen.main.discover

import com.hadysalhab.movid.movies.Movie

sealed class DiscoverMoviesViewState

object Loading : DiscoverMoviesViewState()

object PaginationLoading : DiscoverMoviesViewState()

data class Error(val errorMessage: String) : DiscoverMoviesViewState()

data class DiscoverMoviesLoaded(
    val movies: List<Movie>
) : DiscoverMoviesViewState()