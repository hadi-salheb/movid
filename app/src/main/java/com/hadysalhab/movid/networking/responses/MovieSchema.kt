package com.hadysalhab.movid.networking.responses

import com.google.gson.annotations.SerializedName

data class MovieSchema(
    val id: Long,
    val title: String,
    @SerializedName("poster_path")
    val posterPath: String? = null,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("release_date")
    val releaseDate: String,
    val overview: String
)