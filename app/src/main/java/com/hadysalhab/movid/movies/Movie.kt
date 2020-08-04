package com.hadysalhab.movid.movies


data class Movie (
    val id: Int,
    val title:String,
    val posterPath:String?,
    val voteAverage: Double,
    val voteCount:Int,
    val releaseDate:String?="",
    val overview:String?
    )

data class MovieInfo (
    val adult: Boolean,
    val backdropPath: String?,
    val budget: Int,
    val genres: List<Genres>,
    val homepage: String?,
    val id: Int,
    val imdbID: String?,
    val originalLanguage: String,
    val overview: String?,
    val popularity: Double,
    val posterPath: String?,
    val releaseDate: String?="",
    val revenue: Int,
    val runtime: Int?,
    val status: String,
    val tagLine: String?,
    val title: String,
    val voteAvg: Double,
    val voteCount: Int
)

data class Genres(
    val id: Int,
    val name: String
)