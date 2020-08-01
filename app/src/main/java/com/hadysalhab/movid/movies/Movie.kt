package com.hadysalhab.movid.movies

import com.google.gson.annotations.SerializedName
import com.hadysalhab.movid.networking.responses.Genres

data class Movie (
    val id: Int,
    val title:String,
    val posterPath:String?,
    val voteAverage: Double,
    val voteCount:Int,
    val releaseDate:String,
    val overview:String?
    )