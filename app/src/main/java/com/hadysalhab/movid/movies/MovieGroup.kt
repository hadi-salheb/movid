package com.hadysalhab.movid.movies

import com.hadysalhab.movid.networking.movies.MovieSchema

data class MovieGroup (
    val movieGroupType:MovieGroupType,
    val movies:List<Movie>
)