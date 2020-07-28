package com.hadysalhab.movid.movies

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponse

class FetchNowPlayingMoviesUseCase(private val tmdbApi: TmdbApi) {
    fun fetchNowPlayingMoviesSync(): ApiResponse<MoviesResponse> = try {
        val res = tmdbApi.fetchNowPlayingMovies().execute()
        ApiResponse.create<MoviesResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}