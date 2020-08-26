package com.hadysalhab.movid.movies


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

    fun updateMoviesResponseByGroupType(moviesResponse: MoviesResponse, groupType: GroupType) {
        when (groupType) {
            GroupType.POPULAR -> updatePopularMovies(moviesResponse)
            GroupType.NOW_PLAYING -> updateNowPlayingMovies(moviesResponse)
            GroupType.UPCOMING -> updateUpcomingMovies(moviesResponse)
            GroupType.TOP_RATED -> updateTopRatedMovies(moviesResponse)
            else -> throw RuntimeException("GroupType $groupType not supported in movie store")
        }
    }

    fun getMoviesResponseByGroupType(groupType: GroupType): MoviesResponse = when (groupType) {
        GroupType.POPULAR -> popularMovies
        GroupType.NOW_PLAYING -> nowPlayingMovies
        GroupType.UPCOMING -> upcomingMovies
        GroupType.TOP_RATED -> topRatedMovies
        else -> throw RuntimeException("GroupType $groupType not supported in movie store")
    }

}



