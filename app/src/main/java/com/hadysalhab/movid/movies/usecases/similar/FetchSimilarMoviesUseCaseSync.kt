package com.hadysalhab.movid.movies.usecases.similar

import com.hadysalhab.movid.common.usecases.BaseSimilarRecommendedMoviesUseCase
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import retrofit2.Response

class FetchSimilarMoviesUseCaseSync(
    private val tmdbApi: TmdbApi,
    errorMessageHandler: ErrorMessageHandler,
    schemaToModelHelper: SchemaToModelHelper
) : BaseSimilarRecommendedMoviesUseCase(errorMessageHandler, schemaToModelHelper) {
    override fun getGroupType(): GroupType = GroupType.SIMILAR_MOVIES

    override fun fetchApi(page: Int, movieId: Int): Response<MoviesResponseSchema> =
        tmdbApi.fetchSimilarMovies(
            page = page,
            movieId = movieId
        ).execute()

}