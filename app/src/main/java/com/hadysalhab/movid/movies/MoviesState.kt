package com.hadysalhab.movid.movies

data class MoviesState(
    val popularMovies: MoviesResponse = MoviesResponse(0, 0, 0, null, GroupType.POPULAR),
    val topRatedMovies: MoviesResponse = MoviesResponse(0, 0, 0, null, GroupType.TOP_RATED),
    val upcomingMovies: MoviesResponse = MoviesResponse(0, 0, 0, null, GroupType.UPCOMING),
    val nowPlayingMovies: MoviesResponse = MoviesResponse(0, 0, 0, null, GroupType.NOW_PLAYING),
    val movieDetailList: List<MovieDetail> = emptyList()
)