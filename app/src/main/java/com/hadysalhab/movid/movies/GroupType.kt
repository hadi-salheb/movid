package com.hadysalhab.movid.movies

enum class GroupType(val value: String) {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    UPCOMING("upcoming"),
    LATEST("latest"),
    NOW_PLAYING("now_playing"),
    SIMILAR_MOVIES("similar"),
    RECOMMENDED_MOVIES("recommendations"),
    CAST("cast")
}