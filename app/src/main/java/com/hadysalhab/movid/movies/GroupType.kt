package com.hadysalhab.movid.movies

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class GroupType(var value: String) : Parcelable {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    UPCOMING("upcoming"),
    NOW_PLAYING("now_playing"),
    SIMILAR_MOVIES("similar"),
    RECOMMENDED_MOVIES("recommendations"),
    CAST("cast"),
    FAVORITES("favorites"),
    WATCHLIST("watchlist"),
    COLLECTION("collection")
}