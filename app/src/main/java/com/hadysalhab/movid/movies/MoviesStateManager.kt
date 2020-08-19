package com.hadysalhab.movid.movies

import com.hadysalhab.movid.common.time.TimeProvider

private const val MOVIE_CACHE_TIMEOUT_MS = 24 * 60 * 60 * 1000
private const val MOVIE_DETAIL_CACHE_TIMEOUT_MS = 24 * 60 * 60 * 1000

class MoviesStateManager constructor(private val timeProvider: TimeProvider) {
    private val _movieDetailList = mutableListOf<MovieDetail>()
    val movieDetailList: List<MovieDetail>
        get() = _movieDetailList

    fun isMovieDetailAvailable(movieId: Int): Boolean {
        return if (_movieDetailList.any { it.details.id == movieId }) {
            val currentMovieDetail = _movieDetailList.find { it.details.id == movieId }
            timeProvider.currentTimestamp - MOVIE_DETAIL_CACHE_TIMEOUT_MS < currentMovieDetail!!.timeStamp!!
        } else {
            false
        }
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

    lateinit var popularMovies: MoviesResponse
    lateinit var topRatedMovies: MoviesResponse
    lateinit var upcomingMovies: MoviesResponse
    lateinit var nowPlayingMovies: MoviesResponse

    val arePopularMoviesValid: Boolean
        get() = this::popularMovies.isInitialized && (timeProvider.currentTimestamp - MOVIE_CACHE_TIMEOUT_MS) < popularMovies.timeStamp!!
    val areTopRatedMoviesValid: Boolean
        get() = this::topRatedMovies.isInitialized && (timeProvider.currentTimestamp - MOVIE_CACHE_TIMEOUT_MS) < topRatedMovies.timeStamp!!
    val areUpcomingMoviesValid: Boolean
        get() = this::upcomingMovies.isInitialized && (timeProvider.currentTimestamp - MOVIE_CACHE_TIMEOUT_MS) < upcomingMovies.timeStamp!!
    val areNowPlayingMoviesValid: Boolean
        get() = this::nowPlayingMovies.isInitialized && (timeProvider.currentTimestamp - MOVIE_CACHE_TIMEOUT_MS) < nowPlayingMovies.timeStamp!!


    fun updatePopularMovies(popular: MoviesResponse) {
        popularMovies = MoviesResponse(
            popular.page,
            popular.totalResults,
            popular.total_pages,
            popular.movies,
            popular.tag
        ).apply {
            timeStamp = timeProvider.currentTimestamp
        }
    }

    fun updateTopRatedMovies(topRated: MoviesResponse) {
        topRatedMovies = MoviesResponse(
            topRated.page,
            topRated.totalResults,
            topRated.total_pages,
            topRated.movies,
            topRated.tag
        ).apply {
            timeStamp = timeProvider.currentTimestamp
        }
    }

    fun updateUpcomingMovies(upcoming: MoviesResponse) {
        upcomingMovies = MoviesResponse(
            upcoming.page,
            upcoming.totalResults,
            upcoming.total_pages,
            upcoming.movies,
            upcoming.tag
        ).apply {
            timeStamp = timeProvider.currentTimestamp
        }
    }

    fun updateNowPlayingMovies(nowPlaying: MoviesResponse) {
        nowPlayingMovies = MoviesResponse(
            nowPlaying.page,
            nowPlaying.totalResults,
            nowPlaying.total_pages,
            nowPlaying.movies,
            nowPlaying.tag
        ).apply {
            timeStamp = timeProvider.currentTimestamp
        }
    }
}