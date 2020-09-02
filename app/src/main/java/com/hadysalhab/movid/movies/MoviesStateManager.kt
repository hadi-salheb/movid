package com.hadysalhab.movid.movies

import com.google.gson.Gson


class MoviesStateManager(private var moviesState: MoviesState, private val gson: Gson) {
    private val LOCK = Object()

    // deep copy to avoid any client effect on the global store. Only MoviesStateManager is allowed to do so
    fun upsertMovieDetailToList(movieDetail: MovieDetail) {
        synchronized(LOCK) {
            if (movieDetail.timeStamp == null) {
                throw RuntimeException("MovieDetail should have a timestamp to add it to the store!!!")
            }
            val updatedList =
                moviesState.movieDetailList.filter { it.details.id != movieDetail.details.id }
                    .toMutableList().apply {
                        add(movieDetail.deepCopy(gson))
                    }
            moviesState = moviesState.copy(
                movieDetailList = updatedList
            )
        }
    }

    // deep copy to avoid any client effect on the global store. Only MoviesStateManager is allowed to do so
    private fun updatePopularMovies(popular: MoviesResponse) {
        moviesState = moviesState.copy(popularMovies = popular.deepCopy(gson))
    }

    // deep copy to avoid any client effect on the global store. Only MoviesStateManager is allowed to do so
    private fun updateTopRatedMovies(topRated: MoviesResponse) {
        moviesState = moviesState.copy(topRatedMovies = topRated.deepCopy(gson))
    }

    // deep copy to avoid any client effect on the global store. Only MoviesStateManager is allowed to do so
    private fun updateUpcomingMovies(upcoming: MoviesResponse) {
        moviesState = moviesState.copy(upcomingMovies = upcoming.deepCopy(gson))
    }

    // deep copy to avoid any client effect on the global store. Only MoviesStateManager is allowed to do so
    private fun updateNowPlayingMovies(nowPlaying: MoviesResponse) {
        moviesState = moviesState.copy(nowPlayingMovies = nowPlaying.deepCopy(gson))
    }

    fun updateMoviesResponseByGroupType(moviesResponse: MoviesResponse) {
        synchronized(LOCK) {
            if (moviesResponse.timeStamp == null) {
                throw RuntimeException("MoviesResponse should have a timeStamp to add it to the store")
            }
            when (moviesResponse.tag) {
                GroupType.POPULAR -> updatePopularMovies(moviesResponse)
                GroupType.NOW_PLAYING -> updateNowPlayingMovies(moviesResponse)
                GroupType.UPCOMING -> updateUpcomingMovies(moviesResponse)
                GroupType.TOP_RATED -> updateTopRatedMovies(moviesResponse)
                else -> throw RuntimeException("GroupType ${moviesResponse.tag} not supported in movie store")
            }
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
    fun getTopRatedMovies() = moviesState.topRatedMovies.deepCopy(gson)
    fun getNowPlayingMovies() = moviesState.nowPlayingMovies.deepCopy(gson)
    fun getUpcomingMovies() = moviesState.upcomingMovies.deepCopy(gson)
    fun getPopularMovies() = moviesState.popularMovies.deepCopy(gson)
    fun getMovieDetailById(movieId: Int): MovieDetail? =
        moviesState.movieDetailList.find { it.details.id == movieId }?.deepCopy(gson)


}



