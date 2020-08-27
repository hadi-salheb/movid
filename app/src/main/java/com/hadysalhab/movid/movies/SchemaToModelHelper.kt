package com.hadysalhab.movid.movies

import com.hadysalhab.movid.networking.responses.MovieSchema
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import com.hadysalhab.movid.networking.responses.ReviewSchema
import com.hadysalhab.movid.networking.responses.ReviewsSchema

class SchemaToModelHelper {
    fun getMoviesResponseFromSchema(
        groupType: GroupType,
        moviesResponseSchema: MoviesResponseSchema
    ) = MoviesResponse(
        moviesResponseSchema.page,
        moviesResponseSchema.totalResults,
        moviesResponseSchema.total_pages,
        getMovies(moviesResponseSchema.movies),
        groupType
    )

    private fun getMovies(moviesSchema: List<MovieSchema>): MutableList<Movie> {
        val movies = mutableListOf<Movie>()
        movies.addAll(moviesSchema.map { movieSchema ->
            with(movieSchema) {
                Movie(
                    id,
                    title,
                    posterPath,
                    backdropPath,
                    voteAvg,
                    voteCount,
                    releaseDate,
                    overview
                )
            }
        })
        return movies
    }

    fun getReviewsResponseFromSchema(body: ReviewsSchema) = ReviewResponse(
        body.id,
        body.page,
        getReviewsFromSchema(body.review),
        body.totalPages,
        body.totalPages
    )

    private fun getReviewsFromSchema(reviewSchemaList: List<ReviewSchema>): List<Review> =
        reviewSchemaList.map { reviewSchema ->
            with(reviewSchema) {
                Review(id, author, content, url)
            }
        }
}