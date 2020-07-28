package com.hadysalhab.movid.movies

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.CreateSessionResponse
import com.hadysalhab.movid.networking.responses.MoviesResponse

class FetchTopRatedMoviesUseCase(private val tmdbApi: TmdbApi) {
    fun fetchTopRatedMoviesSync(): ApiResponse<MoviesResponse> = try {
        val res = tmdbApi.fetchTopRatedMovies().execute()
        ApiResponse.create<MoviesResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}