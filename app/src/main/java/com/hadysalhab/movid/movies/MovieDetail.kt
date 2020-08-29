package com.hadysalhab.movid.movies

data class MovieDetail(
    val details: MovieInfo,
    val credits: Credits,
    val reviewResponse: ReviewResponse,
    val images: Images,
    val accountStates: AccountStates,
    val similar: MoviesResponse,
    val recommendations: MoviesResponse,
    val videosResponse: VideosResponse
) {
    var timeStamp: Long? = null
}

data class VideosResponse(
    val videos: List<Video>
)

data class Video(
    val id: String,
    val type: String,
    val site: String,
    val key: String
)

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

data class ReviewResponse(
    val id: Int,
    val page: Int,
    var reviews: List<Review>,
    val totalPages: Int,
    val totalResults: Int
) {
    fun deepCopy(): ReviewResponse = with(this) {
        ReviewResponse(id, page, reviews.map { review ->
            review.copy()
        }, totalPages, totalResults)
    }
}

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
