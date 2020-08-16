package com.hadysalhab.movid.movies

import com.hadysalhab.movid.common.time.TimeProvider

class MoviesStateManager constructor(private val timeProvider: TimeProvider) {
    private var movieGroupListTimeStamp: Long? = null
    private val MOVIE_GROUPS_CACHE_TIMEOUT_MS = 24 * 60 * 60 * 1000
    private val MOVIE_DETAIL_CACHE_TIMEOUT_MS = 24 * 60 * 60 * 1000
    private val _movieDetailList = mutableListOf<MovieDetail>()
    val movieDetailList: List<MovieDetail>
        get() = _movieDetailList

    private val _moviesGroup: MutableList<MovieGroup> = mutableListOf()
    val moviesGroup: List<MovieGroup>
        get() = _moviesGroup


    fun updateMoviesGroup(moviesGroup: List<MovieGroup>) {
        _moviesGroup.clear()
        _moviesGroup.addAll(moviesGroup)
        movieGroupListTimeStamp = timeProvider.currentTimestamp

    }

    fun addMovieDetailToList(movieDetail: MovieDetail) {
        // only add movie that does not exist or if movie exist but its detail are not updated!!
        if (!_movieDetailList.any { it == movieDetail }) {
            //remove old movie detail
            _movieDetailList.filter { it.details.id != movieDetail.details.id }
            movieDetail.timeStamp = timeProvider.currentTimestamp
            _movieDetailList.add(movieDetail)
        }

    }

    fun isMovieDetailAvailable(movieId: Int): Boolean {
        return if (_movieDetailList.any { it.details.id == movieId }) {
            val currentMovieDetail = _movieDetailList.find { it.details.id == movieId }
            timeProvider.currentTimestamp - MOVIE_DETAIL_CACHE_TIMEOUT_MS < currentMovieDetail!!.timeStamp!!
        } else {
            false
        }
    }

    val areMoviesGroupAvailable: Boolean
        get() {
            return _moviesGroup.isNotEmpty() && movieGroupListTimeStamp != null && (timeProvider.currentTimestamp - MOVIE_GROUPS_CACHE_TIMEOUT_MS) < movieGroupListTimeStamp!!
        }

}