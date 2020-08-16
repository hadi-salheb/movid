package com.hadysalhab.movid.movies

class MoviesStateManager {
    val movies = mutableListOf<MovieDetail>()
    var moviesGroup: List<MovieGroup> = emptyList()
    fun areMoviesAvailabe() = moviesGroup.isNotEmpty()
}