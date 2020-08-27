package com.hadysalhab.movid.movies.usecases.reviews

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import com.hadysalhab.movid.networking.responses.ReviewsSchema

class FetchReviewsUseCaseSync(private val tmdbApi: TmdbApi) {
    fun fetchReviewsUseCaseSync(page: Int,movieID: Int): ApiResponse<ReviewsSchema> = try {
        val res = tmdbApi.fetchReviewsForMovie(movieID, page).execute()
        ApiResponse.create<ReviewsSchema>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}