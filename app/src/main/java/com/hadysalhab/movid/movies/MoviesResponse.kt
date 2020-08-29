package com.hadysalhab.movid.movies

class MoviesResponse(
    var page: Int,
    var totalResults: Int,
    var total_pages: Int,
    var movies: MutableList<Movie>?,
    val tag: GroupType
) {
    var timeStamp: Long? = null

    fun deepCopy(): MoviesResponse {
        val page = this.page
        val totalPage = this.total_pages
        val totalResults = this.totalResults
        val movies = this.movies!!.map { it.copy() }.toMutableList()
        val tag = this.tag
        return MoviesResponse(page, totalResults, totalPage, movies, tag)
    }
}