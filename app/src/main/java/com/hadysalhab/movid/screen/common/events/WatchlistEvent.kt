package com.hadysalhab.movid.screen.common.events

import com.hadysalhab.movid.movies.MovieDetail


sealed class WatchlistEvent {
    data class AddToWatchlist(val movieDetail: MovieDetail) : WatchlistEvent()
    data class RemoveFromWatchlist(val movieDetail: MovieDetail) : WatchlistEvent()
}