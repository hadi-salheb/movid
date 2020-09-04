package com.hadysalhab.movid.movies.usecases.nowplaying

import com.hadysalhab.movid.common.usecases.BaseFeaturedMoviesUseCase
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import retrofit2.Response

class FetchNowPlayingMoviesUseCaseSync(
    private val tmdbApi: TmdbApi
) : BaseFeaturedMoviesUseCase() {
    override fun fetchMoviesFromApi(): Response<MoviesResponseSchema> =
        tmdbApi.fetchNowPlayingMovies(
            page = this.page,
            region = this.region
        ).execute()
}