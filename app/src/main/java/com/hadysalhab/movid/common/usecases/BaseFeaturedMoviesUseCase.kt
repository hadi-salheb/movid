package com.hadysalhab.movid.common.usecases

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import retrofit2.Response

// TopRated,UpComing,NowPlaying,Popular
abstract class BaseFeaturedMoviesUseCase {
    protected var page = 1
    lateinit var region: String
    fun fetchFeaturedMoviesUseCase(
        page: Int = 1,
        region: String
    ): ApiResponse<MoviesResponseSchema> {
        this.page = page
        this.region = region
        return try {
            val res = fetchMoviesFromApi()
            ApiResponse.create(res)
        } catch (err: Throwable) {
            ApiResponse.create(err)
        }
    }

    protected abstract fun fetchMoviesFromApi(): Response<MoviesResponseSchema>

}