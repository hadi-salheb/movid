package com.hadysalhab.movid.screen.main.search

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.hadysalhab.movid.R
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
enum class Genre(val id: Int, val genreName: String, @DrawableRes val icon: Int) : Parcelable {
    ACTION(28, "Action", R.drawable.action),
    ADVENTURE(12, "Adventure", R.drawable.adventure),
    ANIMATION(16, "Animation", R.drawable.animation),
    COMEDY(35, "Comedy", R.drawable.comedy),
    CRIME(80, "Crime", R.drawable.crime),
    DOCUMENTARY(99, "Documentary", R.drawable.documentary),
    DRAMA(18, "Drama", R.drawable.drama),
    FAMILY(10751, "Family", R.drawable.family),
    FANTASY(14, "Fantasy", R.drawable.fantasy),
    HISTORY(36, "History", R.drawable.history),
    HORROR(27, "Horror", R.drawable.horror),
    MUSIC(10402, "Music", R.drawable.music),
    MYSTERY(9648, "Mystery", R.drawable.mystery),
    ROMANCE(10749, "Romance", R.drawable.romance),
    SCIENCE_FICTION(878, "Science Fiction", R.drawable.science_fiction),
    THRILLER(53, "Thriller", R.drawable.thriller),
    WAR(10752, "War", R.drawable.war);

    fun getFormattedName(): String = genreName.toUpperCase(Locale.ROOT)
}