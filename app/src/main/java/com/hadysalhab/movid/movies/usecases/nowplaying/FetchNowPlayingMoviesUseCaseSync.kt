package com.hadysalhab.movid.movies.usecases.nowplaying

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema

class FetchNowPlayingMoviesUseCaseSync(private val tmdbApi: TmdbApi) {
    fun fetchNowPlayingMoviesSync(region: String): ApiResponse<MoviesResponseSchema> = try {
        val res = tmdbApi.fetchNowPlayingMovies(region = region).execute()
        ApiResponse.create<MoviesResponseSchema>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}