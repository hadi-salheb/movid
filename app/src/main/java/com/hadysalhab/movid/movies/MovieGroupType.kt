package com.hadysalhab.movid.movies

enum class MovieGroupType(val value: String) {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    UPCOMING("upcoming"),
    LATEST("latest"),
    NOW_PLAYING("now_playing"),
    SIMILAR_MOVIES("similar_movies"),
    RECOMMENDED_MOVIES("recommended_movies")
}