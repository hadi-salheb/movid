package com.hadysalhab.movid.movies

class MoviesStateManager {
    var moviesGroup: List<MovieGroup> = emptyList()
    fun areMoviesAvailabe() = moviesGroup.isNotEmpty()
}