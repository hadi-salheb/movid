package com.hadysalhab.movid.screen.main.featured

import com.hadysalhab.movid.movies.MoviesResponse

sealed class FeaturedViewState

object Loading : FeaturedViewState()

data class Error(val errorMessage: String) : FeaturedViewState()

data class FeaturedLoaded(
    val moviesResponseList: List<MoviesResponse>
) : FeaturedViewState()