package com.hadysalhab.movid.screen.common.events

import com.hadysalhab.movid.movies.MovieDetail


sealed class FavoritesEvent {
    data class AddMovieToFav(val movieDetail: MovieDetail) : FavoritesEvent()
    data class RemoveMovieFromFav(val movieDetail: MovieDetail) : FavoritesEvent()
}