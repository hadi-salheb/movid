package com.hadysalhab.movid.movies

data class MovieDetail(
    val details: MovieInfo,
    val credits: Credits,
    val reviews: Reviews,
    val images: Images,
    val accountStates: AccountStates,
    val similar: MoviesResponse,
    val recommendations: MoviesResponse
) {
    var timeStamp: Long? = null
}


data class Credits(
    val id: Int,
    val cast: List<Cast>,
    val crew: List<Crew>
)

data class Cast(
    val castID: Int,
    val character: String,
    val creditID: String,
    val id: Int,
    val name: String,
    val profilePath: String?
)

data class Crew(
    val creditID: String,
    val department: String,
    val id: Int,
    val job: String,
    val name: String,
    val profilePath: String?
)

data class Reviews(
    val id: Int,
    val page: Int,
    val review: List<Review>,
    val totalPages: Int,
    val totalResults: Int
)

data class Review(
    val id: String,
    val author: String,
    val content: String,
    val url: String
)

data class Images(
    val backdrops: List<Backdrops>
)

data class Backdrops(
    val filePath: String
)

data class AccountStates(
    val id: Int,
    val favorite: Boolean,
    val watchlist: Boolean
)
