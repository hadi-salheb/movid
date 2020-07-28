package com.hadysalhab.movid.movies

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.CreateSessionResponse
import com.hadysalhab.movid.networking.responses.MoviesResponse

class FetchUpcomingMoviesUseCase(private val tmdbApi: TmdbApi) {
    fun fetchUpcomingMoviesSync(): ApiResponse<MoviesResponse> = try {
        val res = tmdbApi.fetchUpcomingMovies().execute()
        ApiResponse.create<MoviesResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}