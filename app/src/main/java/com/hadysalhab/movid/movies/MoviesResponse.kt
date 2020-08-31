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
        val page = this.page
        val totalPage = this.total_pages
        val totalResults = this.totalResults
        val movies = if (this.movies == null) null else this.movies.map { it.copy() }
        val tag = this.tag
        return MoviesResponse(page, totalResults, totalPage, movies, tag).apply {
            timeStamp = this@MoviesResponse.timeStamp
        }
    }

    fun withMovies(movies: List<Movie>): MoviesResponse {
        val page = this.page
        val totalPage = this.total_pages
        val totalResults = this.totalResults
        val movies = movies
        val tag = this.tag
        return MoviesResponse(page, totalResults, totalPage, movies, tag).apply {
            timeStamp = this@MoviesResponse.timeStamp
        }
    }
}