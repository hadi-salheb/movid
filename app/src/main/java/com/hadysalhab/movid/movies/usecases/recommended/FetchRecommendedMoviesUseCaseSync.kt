package com.hadysalhab.movid.movies.usecases.recommended

import com.hadysalhab.movid.common.usecases.BaseSimilarRecommendedMoviesUseCase
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import retrofit2.Response

class FetchRecommendedMoviesUseCaseSync(
    private val tmdbApi: TmdbApi
) : BaseSimilarRecommendedMoviesUseCase() {
    override fun fetchApi(page: Int, movieId: Int): Response<MoviesResponseSchema> =
        tmdbApi.fetchRecommendedMovies(
            page = page,
            movieId = movieId
        ).execute()

}