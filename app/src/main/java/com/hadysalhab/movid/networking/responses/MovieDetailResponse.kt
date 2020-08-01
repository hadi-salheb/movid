package com.hadysalhab.movid.networking.responses

import com.google.gson.annotations.SerializedName

data class MovieDetailResponse(
    val details: MovieSchema,
    val credits: Credits,
    val reviews: Reviews,
    val images: Images,
    @SerializedName("account_states")
    val accountStates: AccountStates,
    val similar: MoviesResponse
)


data class Credits(
    val id: Int,
    val cast: List<Cast>,
    val crew: List<Crew>
)


data class Genres(
    val id: Int,
    val name: String
)

data class Cast(
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

data class Crew(
    @SerializedName("credit_id")
    val creditID: String,
    val department: String,
    val id: Int,
    val job: String,
    val name: String,
    @SerializedName("profile_path")
    val profilePath: String?
)

data class Reviews(
    val id: Int,
    val page: Int,
    @SerializedName("results")
    val review: List<Review>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

data class Review(
    val id: String,
    val author: String,
    val content: String,
    val url: String
)

data class Images(
    val id: Int,
    val backdrops: Backdrops
)

data class Backdrops(
    @SerializedName("file_path")
    val filePath: String
)

data class AccountStates(
    val id: Int,
    val favorite: Boolean,
    val watchlist: Boolean
)
