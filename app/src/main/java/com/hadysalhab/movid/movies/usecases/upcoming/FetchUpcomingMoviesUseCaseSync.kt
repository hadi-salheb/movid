package com.hadysalhab.movid.movies.usecases.upcoming

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema

class FetchUpcomingMoviesUseCaseSync(private val tmdbApi: TmdbApi) {
    fun fetchUpcomingMoviesSync(region: String, page: Int = 1): ApiResponse<MoviesResponseSchema> =
        try {
            val res = tmdbApi.fetchUpcomingMovies(region = region, page = page).execute()
            ApiResponse.create<MoviesResponseSchema>(res)
        } catch (err: Throwable) {
            ApiResponse.create(err)
        }
}