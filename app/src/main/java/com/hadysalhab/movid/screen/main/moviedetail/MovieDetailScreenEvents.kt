package com.hadysalhab.movid.screen.main.moviedetail

sealed class MovieDetailScreenEvents
data class ShowUserToastMessage(val toastMessage: String) : MovieDetailScreenEvents()