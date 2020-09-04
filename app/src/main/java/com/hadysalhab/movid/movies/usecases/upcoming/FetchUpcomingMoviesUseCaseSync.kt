package com.hadysalhab.movid.movies.usecases.upcoming

import com.hadysalhab.movid.common.usecases.BaseFeaturedMoviesUseCase
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import retrofit2.Response

class FetchUpcomingMoviesUseCaseSync(
    private val tmdbApi: TmdbApi
) : BaseFeaturedMoviesUseCase() {
    override fun fetchMoviesFromApi(): Response<MoviesResponseSchema> = tmdbApi.fetchUpcomingMovies(
        page = this.page,
        region = this.region
    ).execute()
}