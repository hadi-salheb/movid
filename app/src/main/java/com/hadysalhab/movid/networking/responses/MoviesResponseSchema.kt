package com.hadysalhab.movid.networking.responses

import com.google.gson.annotations.SerializedName

data class MoviesResponseSchema(
    var page: Int,
    @SerializedName("total_results")
    val totalResults: Int,
    @SerializedName("total_pages")
    val total_pages: Int,
    @SerializedName("results")
    val movies: List<MovieSchema>
)