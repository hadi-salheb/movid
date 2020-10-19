package com.hadysalhab.movid.movies

import com.google.gson.Gson


class MoviesStateManager(
    private val gson: Gson
) {
    private var moviesState: MoviesState = MoviesState()


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


    fun updateMoviesResponse(moviesResponse: MoviesResponse) {
        synchronized(LOCK) {
            if (moviesResponse.page > 1) {
                return
            }
            if (moviesResponse.timeStamp == null) {
                throw RuntimeException("MovieDetail should have a timestamp to add it to the store!!!")
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

    private fun updatePopularMovies(popular: MoviesResponse) {
        moviesState = moviesState.copy(popularMovies = popular.deepCopy(gson))
    }

    private fun updateTopRatedMovies(topRated: MoviesResponse) {
        moviesState = moviesState.copy(topRatedMovies = topRated.deepCopy(gson))
    }

    private fun updateUpcomingMovies(upcoming: MoviesResponse) {
        moviesState = moviesState.copy(upcomingMovies = upcoming.deepCopy(gson))
    }

    private fun updateNowPlayingMovies(nowPlaying: MoviesResponse) {
        moviesState = moviesState.copy(nowPlayingMovies = nowPlaying.deepCopy(gson))
    }

    fun getMoviesResponseByGroupType(groupType: GroupType): MoviesResponse = when (groupType) {
        GroupType.POPULAR -> getPopularMovies()
        GroupType.NOW_PLAYING -> getNowPlayingMovies()
        GroupType.UPCOMING -> getUpcomingMovies()
        GroupType.TOP_RATED -> getTopRatedMovies()
        else -> throw RuntimeException("GroupType $groupType not supported in movie store")
    }

    private fun getTopRatedMovies() = moviesState.topRatedMovies.deepCopy(gson)
    private fun getNowPlayingMovies() = moviesState.nowPlayingMovies.deepCopy(gson)
    private fun getUpcomingMovies() = moviesState.upcomingMovies.deepCopy(gson)
    private fun getPopularMovies() = moviesState.popularMovies.deepCopy(gson)
    fun getMovieDetailById(movieId: Int): MovieDetail? =
        moviesState.movieDetailList.find { it.details.id == movieId }?.deepCopy(gson)

    fun getFeaturedMovies() =
        synchronized(LOCK) {
            listOf(
                getTopRatedMovies(),
                getNowPlayingMovies(),
                getUpcomingMovies(),
                getPopularMovies()
            )
        }

    fun clearMovies() {
        synchronized(LOCK) {
            moviesState = MoviesState()
        }
    }
}



