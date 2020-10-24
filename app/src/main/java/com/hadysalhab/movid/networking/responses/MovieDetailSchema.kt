package com.hadysalhab.movid.networking.responses

import com.google.gson.annotations.SerializedName

data class MovieDetailSchema(
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    val budget: Long,
    val genres: List<GenresSchema>,
    val homepage: String?,
    val id: Int,
    @SerializedName("imdb_id")
    val imdbID: String?,
    @SerializedName("original_language")
    val originalLanguage: String,
    val overview: String?,
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String? = "",
    val revenue: Long,
    val runtime: Int?,
    val status: String,
    @SerializedName("tagline")
    val tagLine: String?,
    val title: String,
    @SerializedName("vote_average")
    val voteAvg: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    val credits: CreditsSchema,
    val reviews: ReviewsSchema,
    val images: ImagesSchema,
    @SerializedName("account_states")
    val accountStates: AccountStatesSchema?,
    val similar: MoviesResponseSchema,
    val recommendations: MoviesResponseSchema,
    val videos: VideosSchema,
    @SerializedName("belongs_to_collection")
    val belongToCollection: BelongToCollectionSchema? = null
)

data class BelongToCollectionSchema(
    val id: Int,
    val name: String
)

data class VideosSchema(
    @SerializedName("results")
    val videos: List<VideoSchema>
)

data class VideoSchema(
    val id: String,
    val type: String,
    val site: String,
    val key: String
)

data class GenresSchema(
    val id: Int,
    val name: String
)

data class CreditsSchema(
    val id: Int,
    val cast: List<CastSchema>,
    val crew: List<CrewSchema>
)

data class CastSchema(
    @SerializedName("cast_id")
    val castID: Int,
    val character: String,
    @SerializedName("credit_id")
    val creditID: String,
    val id: Int,
    val name: String,
    @SerializedName("profile_path")
    val profilePath: String?
)

data class CrewSchema(
    @SerializedName("credit_id")
    val creditID: String,
    val department: String,
    val id: Int,
    val job: String,
    val name: String,
    @SerializedName("profile_path")
    val profilePath: String?
)

data class ReviewsSchema(
    val id: Int,
    val page: Int,
    @SerializedName("results")
    val review: List<ReviewSchema>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

data class ReviewSchema(
    val id: String,
    val author: String,
    val content: String,
    val url: String
)

data class ImagesSchema(
    val backdrops: List<BackdropsSchema>
)

data class BackdropsSchema(
    @SerializedName("file_path")
    val filePath: String
)

data class AccountStatesSchema(
    val id: Int,
    val favorite: Boolean,
    val watchlist: Boolean
)
