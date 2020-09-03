package com.hadysalhab.movid.movies.usecases.recommended

import com.hadysalhab.movid.common.usecases.BaseSimilarRecommendedMoviesUseCase
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import retrofit2.Response

class FetchRecommendedMoviesUseCaseSync(
    private val tmdbApi: TmdbApi,
    private val errorMessageHandler: ErrorMessageHandler,
    private val schemaToModelHelper: SchemaToModelHelper
) : BaseSimilarRecommendedMoviesUseCase(errorMessageHandler, schemaToModelHelper) {
    override fun getGroupType(): GroupType = GroupType.RECOMMENDED_MOVIES

    override fun fetchApi(page: Int, movieId: Int): Response<MoviesResponseSchema> =
        tmdbApi.fetchRecommendedMovies(
            page = page,
            movieId = movieId
        ).execute()

}