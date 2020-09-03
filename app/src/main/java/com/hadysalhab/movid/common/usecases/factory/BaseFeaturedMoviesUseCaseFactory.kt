package com.hadysalhab.movid.common.usecases.factory

import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.common.usecases.BaseFeaturedMoviesUseCase
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.movies.usecases.nowplaying.FetchNowPlayingMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.popular.FetchPopularMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.toprated.FetchTopRatedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.upcoming.FetchUpcomingMoviesUseCaseSync
import com.hadysalhab.movid.networking.TmdbApi

class BaseFeaturedMoviesUseCaseFactory(
    private val deviceConfigManager: DeviceConfigManager,
    private val dataValidator: DataValidator,
    private val timeProvider: TimeProvider,
    private val moviesStateManager: MoviesStateManager,
    private val errorMessageHandler: ErrorMessageHandler,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val tmdbApi: TmdbApi
) {
    fun createBaseFeaturedMoviesUseCase(groupType: GroupType): BaseFeaturedMoviesUseCase =
        when (groupType) {
            GroupType.POPULAR -> FetchPopularMoviesUseCaseSync(
                tmdbApi,
                errorMessageHandler,
                moviesStateManager,
                schemaToModelHelper,
                timeProvider,
                dataValidator,
                deviceConfigManager
            )
            GroupType.NOW_PLAYING -> FetchNowPlayingMoviesUseCaseSync(
                tmdbApi,
                errorMessageHandler,
                moviesStateManager,
                schemaToModelHelper,
                timeProvider,
                dataValidator,
                deviceConfigManager
            )
            GroupType.TOP_RATED -> FetchTopRatedMoviesUseCaseSync(
                tmdbApi,
                errorMessageHandler,
                moviesStateManager,
                schemaToModelHelper,
                timeProvider,
                dataValidator,
                deviceConfigManager
            )
            GroupType.UPCOMING -> FetchUpcomingMoviesUseCaseSync(
                tmdbApi,
                errorMessageHandler,
                moviesStateManager,
                schemaToModelHelper,
                timeProvider,
                dataValidator,
                deviceConfigManager
            )
            else -> throw RuntimeException("$groupType is not part of featured movies")
        }
}