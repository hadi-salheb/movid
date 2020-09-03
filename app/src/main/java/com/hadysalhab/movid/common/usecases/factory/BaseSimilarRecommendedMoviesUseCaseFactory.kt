package com.hadysalhab.movid.common.usecases.factory

import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.movies.usecases.recommended.FetchRecommendedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.similar.FetchSimilarMoviesUseCaseSync
import com.hadysalhab.movid.networking.TmdbApi

class BaseSimilarRecommendedMoviesUseCaseFactory(
    private val errorMessageHandler: ErrorMessageHandler,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val tmdbApi: TmdbApi
) {
    fun createSimilarRecommendedMoviesUseCase(groupType: GroupType) = when (groupType) {
        GroupType.RECOMMENDED_MOVIES -> FetchRecommendedMoviesUseCaseSync(
            tmdbApi, errorMessageHandler, schemaToModelHelper
        )
        GroupType.SIMILAR_MOVIES -> FetchSimilarMoviesUseCaseSync(
            tmdbApi, errorMessageHandler, schemaToModelHelper
        )
        else -> throw RuntimeException("$groupType is not part of recommended or similar movies use case")
    }
}