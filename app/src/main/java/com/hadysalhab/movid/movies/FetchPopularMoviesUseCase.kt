package com.hadysalhab.movid.movies

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.CreateSessionResponse
import com.hadysalhab.movid.networking.responses.MoviesResponse

class FetchPopularMoviesUseCase(private val tmdbApi: TmdbApi) {
    fun fetchPopularMoviesSync(): ApiResponse<MoviesResponse> = try {
        val res = tmdbApi.fetchPopularMovies().execute()
        ApiResponse.create<MoviesResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}