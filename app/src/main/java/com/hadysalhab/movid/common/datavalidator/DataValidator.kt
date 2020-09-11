package com.hadysalhab.movid.common.datavalidator

import com.hadysalhab.movid.account.AccountResponse
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.MoviesResponse


private const val MOVIE_CACHE_TIMEOUT_MS = 24 * 60 * 60 * 1000
private const val MOVIE_DETAIL_CACHE_TIMEOUT_MS = 24 * 60 * 60 * 1000

class DataValidator(private val timeProvider: TimeProvider) {
    fun isMovieDetailValid(movieDetail: MovieDetail?): Boolean {
        return if (movieDetail != null) {
            timeProvider.currentTimestamp - MOVIE_DETAIL_CACHE_TIMEOUT_MS < movieDetail.timeStamp!!
        } else {
            false
        }
    }

    fun isMoviesResponseValid(moviesResponse: MoviesResponse, region: String): Boolean {
        return moviesResponse.timeStamp != null && (timeProvider.currentTimestamp - MOVIE_CACHE_TIMEOUT_MS) < moviesResponse.timeStamp!! && region == moviesResponse.region
    }

    fun isAccountResponseValid(accountResponse: AccountResponse?): Boolean =
        accountResponse != null

    fun isSessionIdValid(sessionId: String?) = sessionId != null
}