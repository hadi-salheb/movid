package com.hadysalhab.movid.common.usecases.factory

import com.hadysalhab.movid.common.usecases.BaseFeaturedMoviesUseCase
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.usecases.nowplaying.FetchNowPlayingMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.popular.FetchPopularMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.toprated.FetchTopRatedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.upcoming.FetchUpcomingMoviesUseCaseSync
import com.hadysalhab.movid.networking.TmdbApi

class BaseFeaturedMoviesUseCaseFactory(
    private val tmdbApi: TmdbApi
) {
    fun createBaseFeaturedMoviesUseCase(groupType: GroupType): BaseFeaturedMoviesUseCase =
        when (groupType) {
            GroupType.POPULAR -> FetchPopularMoviesUseCaseSync(
                tmdbApi
            )
            GroupType.NOW_PLAYING -> FetchNowPlayingMoviesUseCaseSync(
                tmdbApi
            )
            GroupType.TOP_RATED -> FetchTopRatedMoviesUseCaseSync(
                tmdbApi
            )
            GroupType.UPCOMING -> FetchUpcomingMoviesUseCaseSync(
                tmdbApi
            )
            else -> throw RuntimeException("$groupType is not part of featured movies")
        }
}