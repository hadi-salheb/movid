package com.hadysalhab.movid.account

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema

class AddToFavoriteUseCaseSync(private val tmdbApi: TmdbApi) {
    fun addToFavoriteUseCaseSync(
        region: String,
        page: Int = 1
    ): ApiResponse<MoviesResponseSchema> = try {
        val res = tmdbApi.fetchNowPlayingMovies(region = region, page = page).execute()
        ApiResponse.create<MoviesResponseSchema>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}