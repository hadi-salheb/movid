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


    fun updatePopularMovies(popular: MoviesResponse) {
        updateMoviesResponse(popular, moviesState.popularMovies)
    }

    fun updateTopRatedMovies(topRated: MoviesResponse) {
        updateMoviesResponse(topRated, moviesState.topRatedMovies)
    }

    fun updateUpcomingMovies(upcoming: MoviesResponse) {
        updateMoviesResponse(upcoming, moviesState.upcomingMovies)
    }

    fun updateNowPlayingMovies(nowPlaying: MoviesResponse) {
        updateMoviesResponse(nowPlaying, moviesState.nowPlayingMovies)
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



