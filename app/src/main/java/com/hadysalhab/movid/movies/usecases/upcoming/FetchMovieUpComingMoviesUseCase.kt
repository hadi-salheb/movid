package com.hadysalhab.movid.movies.usecases.upcoming

import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.movies.ErrorMessageHandler
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.movies.usecases.list.FetchMovieListUseCase
import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchMovieUpComingMoviesUseCase(
    private val fetchUpcomingMoviesUseCaseSync: FetchUpcomingMoviesUseCaseSync,
     backgroundThreadPoster: BackgroundThreadPoster,
     uiThreadPoster: UiThreadPoster,
     schemaToModelHelper: SchemaToModelHelper,
     timeProvider: TimeProvider,
     errorMessageHandler: ErrorMessageHandler,
     dataValidator: DataValidator,
     moviesStateManager: MoviesStateManager
) : FetchMovieListUseCase(
    backgroundThreadPoster,
    uiThreadPoster,
    schemaToModelHelper,
    timeProvider,
    errorMessageHandler,
    dataValidator,
    moviesStateManager
) {

    override fun fetchMovieList(
        region: String,
        page: Int,
        movieID: Int?
    ): ApiResponse<MoviesResponseSchema> =
        fetchUpcomingMoviesUseCaseSync.fetchUpcomingMoviesSync(region, page)


    override fun getGroupType(): GroupType = GroupType.UPCOMING

    override fun isMoviePartOfTheMovieStore() = true
}