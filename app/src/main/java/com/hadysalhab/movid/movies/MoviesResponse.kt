package com.hadysalhab.movid.movies

import com.google.gson.Gson

data class MoviesResponse(
    val page: Int,
    val totalResults: Int,
    val total_pages: Int,
    val movies: List<Movie>?,
    val tag: GroupType
) {
    var timeStamp: Long? = null
    var region: String? = null
    fun deepCopy(gson: Gson): MoviesResponse {
        val json = gson.toJson(this)
        return gson.fromJson(json, MoviesResponse::class.java)
    }
}