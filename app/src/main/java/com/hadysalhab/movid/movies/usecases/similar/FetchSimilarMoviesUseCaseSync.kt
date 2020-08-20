package com.hadysalhab.movid.movies.usecases.similar

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema

class FetchSimilarMoviesUseCaseSync(private val tmdbApi: TmdbApi) {
    fun fetchSimilarMoviesUseCaseSync(movieID: Int, page: Int): ApiResponse<MoviesResponseSchema> = try {
        val res = tmdbApi.fetchSimilarMovies(movieID, page).execute()
        ApiResponse.create<MoviesResponseSchema>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}