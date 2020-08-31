package com.hadysalhab.movid.movies

data class MoviesResponse(
    val page: Int,
    val totalResults: Int,
    val total_pages: Int,
    val movies: List<Movie>?,
    val tag: GroupType
) {
    var timeStamp: Long? = null

    fun deepCopy(): MoviesResponse {
        val moviesResponse = this.copy(
            movies = movies?.map { it.copy() }
        )
        moviesResponse.timeStamp = this.timeStamp
        return moviesResponse
    }
}