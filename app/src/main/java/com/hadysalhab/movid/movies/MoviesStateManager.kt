package com.hadysalhab.movid.movies


class MoviesStateManager(private val moviesState: MoviesState) {


    fun getMovieDetailById(movieId: Int) =
        moviesState.movieDetailList.find { it.details.id == movieId }


    fun addMovieDetailToList(movieDetail: MovieDetail) {
        val movieDetailList = mutableListOf<MovieDetail>()
        movieDetailList.apply {
            addAll(moviesState.movieDetailList)
            filter { it.details.id != movieDetail.details.id }
            add(movieDetail)
        }
        moviesState.movieDetailList = movieDetailList
    }

    private fun updatePopularMovies(popular: MoviesResponse) {
        moviesState.popularMovies = popular.deepCopy()
    }

    private fun updateTopRatedMovies(topRated: MoviesResponse) {
        moviesState.topRatedMovies = topRated.deepCopy()
    }

    private fun updateUpcomingMovies(upcoming: MoviesResponse) {
        moviesState.upcomingMovies = upcoming.deepCopy()
    }

    private fun updateNowPlayingMovies(nowPlaying: MoviesResponse) {
        moviesState.nowPlayingMovies = nowPlaying.deepCopy()
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
        GroupType.POPULAR -> moviesState.popularMovies
        GroupType.NOW_PLAYING -> moviesState.nowPlayingMovies
        GroupType.UPCOMING -> moviesState.upcomingMovies
        GroupType.TOP_RATED -> moviesState.topRatedMovies
        else -> throw RuntimeException("GroupType $groupType not supported in movie store")
    }

    fun getTopRatedMovies() = moviesState.topRatedMovies
    fun getNowPlayingMovies() = moviesState.nowPlayingMovies
    fun getUpcomingMovies() = moviesState.upcomingMovies
    fun getPopularMovies() = moviesState.popularMovies


}



