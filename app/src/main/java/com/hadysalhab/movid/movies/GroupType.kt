package com.hadysalhab.movid.movies

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
enum class GroupType(var value: String) : Parcelable {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    UPCOMING("upcoming"),
    NOW_PLAYING("now_playing"),
    SIMILAR_MOVIES("similar"),
    RECOMMENDED_MOVIES("recommendations"),
    CAST("cast"),
    CREW("crew"),
    FAVORITES("favorites"),
    WATCHLIST("watchlist"),
    COLLECTION("collection"),
    SEARCH("search");

    fun getFormattedValue(): String =
        this.value.split('_').joinToString(" ").toUpperCase(Locale.ROOT)

}