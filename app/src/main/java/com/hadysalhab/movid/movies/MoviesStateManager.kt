package com.hadysalhab.movid.movies

import com.hadysalhab.movid.common.time.TimeProvider

private const val MOVIE_CACHE_TIMEOUT_MS = 24 * 60 * 60 * 1000
private const val MOVIE_DETAIL_CACHE_TIMEOUT_MS = 24 * 60 * 60 * 1000

class MoviesStateManager constructor(private val timeProvider: TimeProvider) {
    private var movieGroupListTimeStamp: Long? = null

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
    val popularMovies: MoviesResponse = MoviesResponse(0, 0, 0, null, GroupType.POPULAR)
    val topRatedMovies: MoviesResponse =
        MoviesResponse(0, 0, 0, null, GroupType.TOP_RATED)
    val upcomingMovies: MoviesResponse =
        MoviesResponse(0, 0, 0, null, GroupType.UPCOMING)
    val nowPlayingMovies: MoviesResponse =
        MoviesResponse(0, 0, 0, null, GroupType.NOW_PLAYING)

    val arePopularMoviesAvailable: Boolean
        get() = validateMovies(popularMovies)
    val areTopRatedMoviesAvailable: Boolean
        get() = validateMovies(topRatedMovies)
    val areUpcomingMoviesAvailable: Boolean
        get() = validateMovies(upcomingMovies)
    val areNowPlayingMoviesAvailable: Boolean
        get() = validateMovies(nowPlayingMovies)

    private fun validateMovies(moviesResponse: MoviesResponse): Boolean {
        return moviesResponse.movies != null && moviesResponse.timeStamp != null && (timeProvider.currentTimestamp - MOVIE_CACHE_TIMEOUT_MS) < moviesResponse.timeStamp!!
    }

    fun setPopularMovies(popular: MoviesResponse) {
        popularMovies.apply {
            timeStamp = timeProvider.currentTimestamp
            page = popular.page
            total_pages = popular.total_pages
            movies = mutableListOf()
            movies!!.addAll(popular.movies!!.toList())
        }
    }

    fun setTopRatedMovies(topRated: MoviesResponse) {
        topRatedMovies.apply {
            timeStamp = timeProvider.currentTimestamp
            page = topRated.page
            total_pages = topRated.total_pages
            movies = mutableListOf()
            movies!!.addAll(topRated.movies!!.toList())
        }
    }

    fun setUpcomingMovies(upcoming: MoviesResponse) {
        upcomingMovies.apply {
            timeStamp = timeProvider.currentTimestamp
            page = upcoming.page
            total_pages = upcoming.total_pages
            movies = mutableListOf()
            movies!!.addAll(upcoming.movies!!.toList())
        }
    }

    fun setNowPlayingMovies(nowPlaying: MoviesResponse) {
        nowPlayingMovies.apply {
            timeStamp = timeProvider.currentTimestamp
            page = nowPlaying.page
            total_pages = nowPlaying.total_pages
            movies = mutableListOf()
            movies!!.addAll(nowPlaying.movies!!.toList())
        }
    }
}