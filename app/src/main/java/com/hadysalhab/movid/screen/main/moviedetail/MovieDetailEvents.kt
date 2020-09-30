package com.hadysalhab.movid.screen.main.moviedetail

sealed class MovieDetailEvents
data class ShowUserToastMessage(val toastMessage: String) : MovieDetailEvents()