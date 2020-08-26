package com.hadysalhab.movid.movies

import com.hadysalhab.movid.networking.responses.MovieSchema
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema

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
}