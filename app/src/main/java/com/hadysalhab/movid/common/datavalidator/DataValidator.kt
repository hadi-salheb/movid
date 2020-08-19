package com.hadysalhab.movid.common.datavalidator

import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.MoviesResponse


private const val MOVIE_CACHE_TIMEOUT_MS = 24 * 60 * 60 * 1000
private const val MOVIE_DETAIL_CACHE_TIMEOUT_MS = 24 * 60 * 60 * 1000

class DataValidator(private val timeProvider: TimeProvider) {
    fun isMovieDetailAvailable(movieId: Int, movieDetailList: List<MovieDetail>): Boolean {
        return if (movieDetailList.any { it.details.id == movieId }) {
            val currentMovieDetail = movieDetailList.find { it.details.id == movieId }
            timeProvider.currentTimestamp - MOVIE_DETAIL_CACHE_TIMEOUT_MS < currentMovieDetail!!.timeStamp!!
        } else {
            false
        }
    }

    fun isMoviesResponseValid(moviesResponse: MoviesResponse): Boolean {
        return moviesResponse.timeStamp != null && (timeProvider.currentTimestamp - MOVIE_CACHE_TIMEOUT_MS) < moviesResponse.timeStamp!!
    }

}