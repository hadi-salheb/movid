package com.hadysalhab.movid.screen.main.moviedetail

import com.hadysalhab.movid.movies.MovieDetail

sealed class MovieDetailViewState

object Loading : MovieDetailViewState()
object AccountStateLoading : MovieDetailViewState()

data class Error(val errorMessage: String) : MovieDetailViewState()

data class DetailLoaded(
    val movie: MovieDetail
) : MovieDetailViewState()