package com.hadysalhab.movid.movies.usecases.toprated

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema

class FetchTopRatedMoviesUseCaseSync(private val tmdbApi: TmdbApi) {
    fun fetchTopRatedMoviesSync(region: String,page:Int = 1): ApiResponse<MoviesResponseSchema> = try {
        val res = tmdbApi.fetchTopRatedMovies(region = region,page = page).execute()
        ApiResponse.create<MoviesResponseSchema>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}