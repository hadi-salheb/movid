package com.hadysalhab.movid.movies

data class MoviesState(
    var popularMovies: MoviesResponse = MoviesResponse(0, 0, 0, null, GroupType.POPULAR),
    var topRatedMovies: MoviesResponse = MoviesResponse(0, 0, 0, null, GroupType.TOP_RATED),
    var upcomingMovies: MoviesResponse = MoviesResponse(0, 0, 0, null, GroupType.UPCOMING),
    var nowPlayingMovies: MoviesResponse = MoviesResponse(0, 0, 0, null, GroupType.NOW_PLAYING),
    var movieDetailList: List<MovieDetail> = emptyList()
)