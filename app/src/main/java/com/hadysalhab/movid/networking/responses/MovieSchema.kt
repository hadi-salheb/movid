package com.hadysalhab.movid.networking.responses

import com.google.gson.annotations.SerializedName

data class MovieSchema(
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    val budget: Int,
    val genres: List<Genres>,
    val homepage: String?,
    val id: Int,
    @SerializedName("imdb_id")
    val imdbID: String?,
    @SerializedName("original_language")
    val originalLanguage: String,
    val overview: String?,
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    val revenue: Int,
    val runtime: Int?,
    val status: String,
    @SerializedName("tagline")
    val tagLine: String?,
    val title: String,
    @SerializedName("vote_average")
    val voteAvg: Double,
    @SerializedName("vote_count")
    val voteCount: Int
)