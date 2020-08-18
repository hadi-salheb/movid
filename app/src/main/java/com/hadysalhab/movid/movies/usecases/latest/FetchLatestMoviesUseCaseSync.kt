package com.hadysalhab.movid.movies.usecases.latest

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema

class FetchLatestMoviesUseCaseSync(private val tmdbApi: TmdbApi) {
    fun fetchLatestMoviesSync(): ApiResponse<MoviesResponseSchema> = try {
        val res = tmdbApi.fetchLatestMovies().execute()
        ApiResponse.create<MoviesResponseSchema>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}