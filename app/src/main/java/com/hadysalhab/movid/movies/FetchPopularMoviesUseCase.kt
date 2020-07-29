package com.hadysalhab.movid.movies

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.CreateSessionResponse
import com.hadysalhab.movid.networking.responses.MoviesResponse

class FetchPopularMoviesUseCase(private val tmdbApi: TmdbApi) {
    fun fetchPopularMoviesSync(region:String): ApiResponse<MoviesResponse> = try {
        val res = tmdbApi.fetchPopularMovies(region = region).execute()
        ApiResponse.create<MoviesResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}