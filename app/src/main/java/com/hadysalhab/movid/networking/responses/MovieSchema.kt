package com.hadysalhab.movid.networking.responses

import com.google.gson.annotations.SerializedName

data class MovieSchema(
    val id: Int,
    val overview: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?="",
    val title: String,
    @SerializedName("vote_average")
    val voteAvg: Double,
    @SerializedName("vote_count")
    val voteCount: Int
)