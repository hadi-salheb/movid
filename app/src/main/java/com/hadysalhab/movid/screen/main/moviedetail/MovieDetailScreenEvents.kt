package com.hadysalhab.movid.screen.main.moviedetail

sealed class MovieDetailScreenEvents
data class ShowUserToastMessage(val toastMessage: String) : MovieDetailScreenEvents()
data class ShowRateMovieDialog(
    val movieName: String,
    val currentRating: Double?,
    val movieId: Int
) : MovieDetailScreenEvents()