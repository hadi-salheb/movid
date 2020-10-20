package com.hadysalhab.movid.movies

import com.hadysalhab.movid.account.AccountResponse
import com.hadysalhab.movid.account.Avatar
import com.hadysalhab.movid.account.Gravatar
import com.hadysalhab.movid.common.constants.BACKDROP_SIZE_780
import com.hadysalhab.movid.common.constants.IMAGES_BASE_URL
import com.hadysalhab.movid.networking.responses.*

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

    fun getMovieDetails(schema: MovieDetailSchema, collectionSchema: CollectionSchema? = null) =
        with(schema) {
            MovieDetail(
                getMovieInfo(this),
                getCredits(credits),
                getReviews(reviews),
                getImages(images),
                getAccountState(accountStates),
                getMoviesResponseFromSchema(GroupType.SIMILAR_MOVIES, similar),
                getMoviesResponseFromSchema(GroupType.RECOMMENDED_MOVIES, recommendations),
                getVideos(videos),
                getCollections(collectionSchema)
            )
        }

    private fun getCollections(collectionSchema: CollectionSchema?) =
        if (collectionSchema == null) {
            null
        } else {
            Collection(
                collectionSchema.id,
                collectionSchema.name,
                collectionSchema.overview,
                getMovies(collectionSchema.parts)
            )
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


    private fun getVideos(videosSchema: VideosSchema) = with(videosSchema) {
        VideosResponse(videos.map { videosSchema ->
            with(videosSchema) {
                Video(id, type, site, key)
            }
        })
    }


    private fun getMovieInfo(movieDetailSchema: MovieDetailSchema) = with(movieDetailSchema) {
        MovieInfo(
            adult,
            backdropPath,
            budget,
            getGenres(genres),
            homepage,
            id,
            imdbID,
            originalLanguage,
            overview,
            popularity,
            posterPath,
            releaseDate,
            revenue,
            runtime,
            status,
            tagLine,
            title,
            voteAvg,
            voteCount
        )
    }

    private fun getGenres(genresSchema: List<GenresSchema>) = genresSchema.map { el ->
        Genres(el.id, el.name)
    }

    fun getCredits(creditsSchema: CreditsSchema) = with(creditsSchema) {
        Credits(id, getCasts(cast), getCrews(crew))
    }

    private fun getCasts(castSchemas: List<CastSchema>): List<Cast> = castSchemas.map { el ->
        Cast(
            el.castID,
            el.character,
            el.creditID,
            el.id,
            el.name,
            el.profilePath
        )
    }

    private fun getCrews(crewSchemas: List<CrewSchema>): List<Crew> = crewSchemas.map { el ->
        Crew(
            el.creditID,
            el.department,
            el.id,
            el.job,
            el.name,
            el.profilePath
        )
    }

    private fun getReviews(reviewsSchema: ReviewsSchema) = with(reviewsSchema) {
        ReviewResponse(
            id,
            page,
            getReview(review),
            totalPages,
            totalResults
        )
    }

    private fun getReview(reviewSchema: List<ReviewSchema>) = reviewSchema.map { el ->
        Review(el.id, el.author, el.content, el.url)
    }

    private fun getImages(imagesSchema: ImagesSchema) = with(imagesSchema) {
        Images(getBackdrops(backdrops))
    }

    private fun getBackdrops(backdrops: List<BackdropsSchema>) = backdrops.map { el ->
        Backdrops(IMAGES_BASE_URL + BACKDROP_SIZE_780 + el.filePath)
    }

    private fun getAccountState(accountStatesSchema: AccountStatesSchema) =
        with(accountStatesSchema) {
            AccountStates(id, favorite, watchlist)
        }


    fun getAccountResponse(accountSchema: AccountSchema): AccountResponse = with(accountSchema) {
        AccountResponse(
            id,
            name,
            username,
            includeAdult,
            Avatar(Gravatar(avatarSchema.gravatarSchema.hash)),
            language,
            country
        )
    }

    fun getMovieFromMovieDetail(movieDetail: MovieDetail): Movie = with(movieDetail.details) {
        Movie(id, title, posterPath, backdropPath, voteAvg, voteCount, releaseDate, overview)
    }
}