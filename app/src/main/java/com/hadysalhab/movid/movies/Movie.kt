package com.hadysalhab.movid.movies

import com.google.gson.annotations.SerializedName

data class Movie (
    val id: Long,
    val title:String,
    val posterPath:String,
    val voteAverage: Double,
    val voteCount:Int,
    val releaseDate:String
    )