package com.hadysalhab.movid.movies.usecases.recommended

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema

class FetchRecommendedMoviesUseCaseSync(private val tmdbApi: TmdbApi) {
    fun fetchRecommendedMoviesUseCaseSync(movieID: Int, page: Int): ApiResponse<MoviesResponseSchema> = try {
        val res = tmdbApi.fetchRecommendedMovies(movieID, page).execute()
        ApiResponse.create<MoviesResponseSchema>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}