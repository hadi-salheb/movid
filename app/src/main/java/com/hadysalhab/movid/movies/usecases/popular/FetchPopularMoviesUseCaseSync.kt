package com.hadysalhab.movid.movies.usecases.popular

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema

class FetchPopularMoviesUseCaseSync(private val tmdbApi: TmdbApi) {
    fun fetchPopularMoviesSync(region: String, page: Int = 1): ApiResponse<MoviesResponseSchema> =
        try {
            val res = tmdbApi.fetchPopularMovies(region = region, page = page).execute()
            ApiResponse.create<MoviesResponseSchema>(res)
        } catch (err: Throwable) {
            ApiResponse.create(err)
        }
}