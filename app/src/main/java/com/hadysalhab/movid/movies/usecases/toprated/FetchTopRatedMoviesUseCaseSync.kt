package com.hadysalhab.movid.movies.usecases.toprated

import com.hadysalhab.movid.common.usecases.BaseFeaturedMoviesUseCase
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import retrofit2.Response

class FetchTopRatedMoviesUseCaseSync(
    private val tmdbApi: TmdbApi
) : BaseFeaturedMoviesUseCase() {
    override fun fetchMoviesFromApi(): Response<MoviesResponseSchema> = tmdbApi.fetchTopRatedMovies(
        page = this.page,
        region = this.region
    ).execute()
}