package com.hadysalhab.movid.movies

class MoviesStateManager {
    val movies = mutableListOf<MovieDetail>()
    val moviesGroup: MutableList<MovieGroup> = mutableListOf()
    fun areMoviesAvailabe() = moviesGroup.isNotEmpty()
}