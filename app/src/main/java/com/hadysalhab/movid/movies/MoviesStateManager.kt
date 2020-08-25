package com.hadysalhab.movid.movies

import com.hadysalhab.movid.common.time.TimeProvider


class MoviesStateManager {
    private val _movieDetailList = mutableListOf<MovieDetail>()
    val movieDetailList: List<MovieDetail>
        get() = _movieDetailList

    fun addMovieDetailToList(movieDetail: MovieDetail) {
            _movieDetailList.filter { it.details.id != movieDetail.details.id }
            _movieDetailList.add(movieDetail)
    }

    val popularMovies: MoviesResponse = MoviesResponse(0, 0, 0, null, GroupType.POPULAR)
    val topRatedMovies: MoviesResponse = MoviesResponse(0, 0, 0, null, GroupType.TOP_RATED)
    val upcomingMovies: MoviesResponse = MoviesResponse(0, 0, 0, null, GroupType.UPCOMING)
    val nowPlayingMovies: MoviesResponse = MoviesResponse(0, 0, 0, null, GroupType.NOW_PLAYING)

    fun updatePopularMovies(popular: MoviesResponse) {
        updateMoviesResponse(popular, popularMovies)
    }

    fun updateTopRatedMovies(topRated: MoviesResponse) {
        updateMoviesResponse(topRated, topRatedMovies)
    }

    fun updateUpcomingMovies(upcoming: MoviesResponse) {
        updateMoviesResponse(upcoming, upcomingMovies)
    }

    fun updateNowPlayingMovies(nowPlaying: MoviesResponse) {
        updateMoviesResponse(nowPlaying, nowPlayingMovies)
    }

    private fun updateMoviesResponse(
        newMoviesResponse: MoviesResponse,
        oldMoviesResponse: MoviesResponse
    ) {
        oldMoviesResponse.apply {
            page = newMoviesResponse.page
            total_pages = newMoviesResponse.total_pages
            totalResults = newMoviesResponse.totalResults
            movies = mutableListOf()
            movies!!.addAll(newMoviesResponse.movies ?: emptyList())
            timeStamp = newMoviesResponse.timeStamp
        }
    }
}



