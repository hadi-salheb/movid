package com.hadysalhab.movid.movies

data class MoviesResponse(
    var page: Int,
    var totalResults: Int,
    var total_pages: Int,
    var movies: MutableList<Movie>?,
    val tag:GroupType
){
    var timeStamp: Long? = null
}