package com.hadysalhab.movid.movies


class MoviesStateManager(private var moviesState: MoviesState) {
    // deep copy to avoid any client effect on the global store. Only MoviesStateManager is allowed to do so
    fun upsertMovieDetailToList(movieDetail: MovieDetail) {
        /*
         * state = {
         * ...state,
         * moviesRes:[...state.moviesRes, ...newMovies ]
         * }
         *
         * */
        moviesState = moviesState.copy(
            movieDetailList = mutableListOf<MovieDetail>().apply {
                addAll(moviesState.movieDetailList)
                filter { it.details.id != movieDetail.details.id }
                add(movieDetail.deepCopy())
            }
        )
    }

    // deep copy to avoid any client effect on the global store. Only MoviesStateManager is allowed to do so
    private fun updatePopularMovies(popular: MoviesResponse) {
        moviesState = moviesState.copy(popularMovies = popular.deepCopy())
    }

    // deep copy to avoid any client effect on the global store. Only MoviesStateManager is allowed to do so
    private fun updateTopRatedMovies(topRated: MoviesResponse) {
        moviesState = moviesState.copy(topRatedMovies = topRated.deepCopy())
    }

    // deep copy to avoid any client effect on the global store. Only MoviesStateManager is allowed to do so
    private fun updateUpcomingMovies(upcoming: MoviesResponse) {
        moviesState = moviesState.copy(upcomingMovies = upcoming.deepCopy())
    }

    // deep copy to avoid any client effect on the global store. Only MoviesStateManager is allowed to do so
    private fun updateNowPlayingMovies(nowPlaying: MoviesResponse) {
        moviesState = moviesState.copy(nowPlayingMovies = nowPlaying.deepCopy())
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

    // deep copy to avoid any client effect on the global store. Only MoviesStateManager is allowed to do so
    fun getTopRatedMovies() = moviesState.topRatedMovies.deepCopy()
    fun getNowPlayingMovies() = moviesState.nowPlayingMovies.deepCopy()
    fun getUpcomingMovies() = moviesState.upcomingMovies.deepCopy()
    fun getPopularMovies() = moviesState.popularMovies.deepCopy()
    fun getMovieDetailById(movieId: Int) =
        moviesState.movieDetailList.find { it.details.id == movieId }?.deepCopy()

}



