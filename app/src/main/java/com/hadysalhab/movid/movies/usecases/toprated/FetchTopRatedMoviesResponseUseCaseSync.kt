package com.hadysalhab.movid.movies.usecases.toprated

import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.common.usecases.BaseMoviesResponseUseCase
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import retrofit2.Response

class FetchTopRatedMoviesResponseUseCaseSync(
    private val tmdbApi: TmdbApi,
    errorMessageHandler: ErrorMessageHandler,
    moviesStateManager: MoviesStateManager,
    schemaToModelHelper: SchemaToModelHelper,
    timeProvider: TimeProvider,
    dataValidator: DataValidator,
    deviceConfigManager: DeviceConfigManager
) : BaseMoviesResponseUseCase(
    deviceConfigManager,
    dataValidator,
    timeProvider,
    moviesStateManager,
    errorMessageHandler,
    schemaToModelHelper
) {
    override fun fetchMoviesFromApi(): Response<MoviesResponseSchema> = tmdbApi.fetchTopRatedMovies(
        page = this.page,
        region = this.region
    ).execute()

    override fun getGroupType(): GroupType = GroupType.TOP_RATED
}