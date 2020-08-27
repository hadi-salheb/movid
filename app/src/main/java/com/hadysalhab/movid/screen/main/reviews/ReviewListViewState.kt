package com.hadysalhab.movid.screen.main.reviews

import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.Review

sealed class ReviewListViewState

object Loading : ReviewListViewState()

object PaginationLoading : ReviewListViewState()

data class Error(val errorMessage: String) : ReviewListViewState()

data class ReviewListLoaded(
    val reviews: List<Review>
) : ReviewListViewState()