package com.hadysalhab.movid.movies.usecases.popular

import com.hadysalhab.movid.common.usecases.BaseFeaturedMoviesUseCase
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import retrofit2.Response

class FetchPopularMoviesUseCaseSync(
    private val tmdbApi: TmdbApi
) : BaseFeaturedMoviesUseCase() {
    override fun fetchMoviesFromApi(): Response<MoviesResponseSchema> = tmdbApi.fetchPopularMovies(
        page = this.page,
        region = this.region
    ).execute()
}