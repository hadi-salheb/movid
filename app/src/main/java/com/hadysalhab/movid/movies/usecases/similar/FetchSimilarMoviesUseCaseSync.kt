package com.hadysalhab.movid.movies.usecases.similar

import com.hadysalhab.movid.common.usecases.BaseSimilarRecommendedMoviesUseCase
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import retrofit2.Response

class FetchSimilarMoviesUseCaseSync(
    private val tmdbApi: TmdbApi
) : BaseSimilarRecommendedMoviesUseCase() {
    override fun fetchApi(page: Int, movieId: Int): Response<MoviesResponseSchema> =
        tmdbApi.fetchSimilarMovies(
            page = page,
            movieId = movieId
        ).execute()

}