package com.hadysalhab.movid.common.usecases.factory

import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.usecases.recommended.FetchRecommendedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.similar.FetchSimilarMoviesUseCaseSync
import com.hadysalhab.movid.networking.TmdbApi

class BaseSimilarRecommendedMoviesUseCaseFactory(
    private val tmdbApi: TmdbApi
) {
    fun createSimilarRecommendedMoviesUseCase(groupType: GroupType) = when (groupType) {
        GroupType.RECOMMENDED_MOVIES -> FetchRecommendedMoviesUseCaseSync(
            tmdbApi
        )
        GroupType.SIMILAR_MOVIES -> FetchSimilarMoviesUseCaseSync(
            tmdbApi
        )
        else -> throw RuntimeException("$groupType is not part of recommended or similar movies use case")
    }
}