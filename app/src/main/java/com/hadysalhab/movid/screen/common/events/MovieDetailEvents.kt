package com.hadysalhab.movid.screen.common.events

import com.hadysalhab.movid.movies.MovieDetail

sealed class MovieDetailEvents(val movieDetail: MovieDetail) {
    data class AddToWatchlist(val movie: MovieDetail) : MovieDetailEvents(movie)
    data class RemoveFromWatchlist(val movie: MovieDetail) : MovieDetailEvents(movie)
    data class AddMovieToFav(val movie: MovieDetail) : MovieDetailEvents(movie)
    data class RemoveMovieFromFav(val movie: MovieDetail) : MovieDetailEvents(movie)
    data class MovieDetailFetched(val movie: MovieDetail) : MovieDetailEvents(movie)
}