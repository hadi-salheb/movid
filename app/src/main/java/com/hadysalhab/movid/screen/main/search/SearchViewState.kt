package com.hadysalhab.movid.screen.main.search

import com.hadysalhab.movid.movies.Movie

sealed class SearchViewState

object Loading : SearchViewState()

object PaginationLoading : SearchViewState()

data class Error(val errorMessage: String) : SearchViewState()

object Genres : SearchViewState()
data class SearchLoaded(
    val movies: List<Movie>
) : SearchViewState()