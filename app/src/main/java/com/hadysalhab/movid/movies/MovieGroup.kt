package com.hadysalhab.movid.movies

data class MovieGroup (
    val movieGroupType:MovieGroupType,
    val movies:List<Movie>
)