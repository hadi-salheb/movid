package com.hadysalhab.movid.movies

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponse

class FetchLatestMoviesUseCase(private val tmdbApi: TmdbApi) {
    fun fetchLatestMoviesSync(): ApiResponse<MoviesResponse> = try {
        val res = tmdbApi.fetchLatestMovies().execute()
        ApiResponse.create<MoviesResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}