package com.hadysalhab.movid.movies.usecases.similar

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

class FetchMovieSimilarMoviesUseCase(
    private val fetchSimilarMoviesUseCaseSync: FetchSimilarMoviesUseCaseSync,
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
    ): ApiResponse<MoviesResponseSchema> {
        if (movieID == null) {
            throw RuntimeException("Cannot fetch similar movies without movieID")
        }
        return fetchSimilarMoviesUseCaseSync.fetchSimilarMoviesUseCaseSync(movieID, page)
    }


    override fun getGroupType(): GroupType = GroupType.SIMILAR_MOVIES

    override fun isMoviePartOfTheMovieStore() = false
}