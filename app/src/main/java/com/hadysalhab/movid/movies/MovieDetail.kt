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

    fun deepCopy(): MovieDetail = this.copy(
        details = details.deepCopy(),
        credits = credits.deepCopy(),
        reviewResponse = reviewResponse.deepCopy(),
        images = images.deepCopy(),
        similar = similar.deepCopy(),
        recommendations = recommendations.deepCopy(),
        videosResponse = videosResponse.deepCopy()
    ).also {
        it.timeStamp = timeStamp
    }
}

data class VideosResponse(
    val videos: List<Video>
) {
    fun deepCopy() = this.copy(videos = videos.map { it.copy() })
}

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
) {
    fun deepCopy() = this.copy(cast = cast.map { it.copy() }, crew = crew.map { it.copy() })
}

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
    fun deepCopy(): ReviewResponse = this.copy(reviews = reviews.map { it.copy() })
}

data class Review(
    val id: String,
    val author: String,
    val content: String,
    val url: String
)

data class Images(
    val backdrops: List<Backdrops>
) {
    fun deepCopy() = this.copy(backdrops = backdrops.map { it.copy() })
}

data class Backdrops(
    val filePath: String
)

data class AccountStates(
    val id: Int,
    val favorite: Boolean,
    val watchlist: Boolean
)
