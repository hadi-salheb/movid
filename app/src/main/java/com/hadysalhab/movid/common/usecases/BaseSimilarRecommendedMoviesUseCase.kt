package com.hadysalhab.movid.common.usecases

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import retrofit2.Response

//Similar,Recommended
abstract class BaseSimilarRecommendedMoviesUseCase {
    fun fetchSimilarRecommendedMoviesUseCase(
        page: Int = 1,
        movieId: Int
    ): ApiResponse<MoviesResponseSchema> = try {
        val res = fetchApi(page, movieId)
        ApiResponse.create(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }

    protected abstract fun fetchApi(page: Int, movieId: Int): Response<MoviesResponseSchema>
}