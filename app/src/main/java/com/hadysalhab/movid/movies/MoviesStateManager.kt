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
        GroupType.POPULAR -> getPopularMovies()
        GroupType.NOW_PLAYING -> getNowPlayingMovies()
        GroupType.UPCOMING -> getUpcomingMovies()
        GroupType.TOP_RATED -> getTopRatedMovies()
        else -> throw RuntimeException("GroupType $groupType not supported in movie store")
    }

    fun getTopRatedMovies() = moviesState.topRatedMovies.deepCopy()
    fun getNowPlayingMovies() = moviesState.nowPlayingMovies.deepCopy()
    fun getUpcomingMovies() = moviesState.upcomingMovies.deepCopy()
    fun getPopularMovies() = moviesState.popularMovies.deepCopy()


}



