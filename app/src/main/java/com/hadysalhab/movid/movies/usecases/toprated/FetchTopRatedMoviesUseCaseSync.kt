package com.hadysalhab.movid.movies.usecases.toprated

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponse

class FetchTopRatedMoviesUseCaseSync(private val tmdbApi: TmdbApi) {
    fun fetchTopRatedMoviesSync(region: String): ApiResponse<MoviesResponse> = try {
        val res = tmdbApi.fetchTopRatedMovies(region = region).execute()
        ApiResponse.create<MoviesResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}