package com.hadysalhab.movid.screen.main.search

import androidx.annotation.DrawableRes
import com.hadysalhab.movid.R

enum class Genre(val id: Int, val genre: String, @DrawableRes val icon: Int) {
    ACTION(28, "Action", R.drawable.ic_action),
    ADVENTURE(12, "Adventure", R.drawable.ic_adventure),
    ANIMATION(16, "Animation", R.drawable.ic_animation),
    COMEDY(35, "Comedy", R.drawable.ic_comedy),
    CRIME(80, "Crime", R.drawable.ic_crime),
    DOCUMENTARY(99, "Documentary", R.drawable.ic_documentary),
    DRAMA(18, "Drama", R.drawable.ic_drama),
    FAMILY(10751, "Family", R.drawable.ic_family),
    FANTASY(14, "Fantasy", R.drawable.ic_fantasy),
    HISTORY(36, "History", R.drawable.ic_history),
//    HORROR(27, "Horror", R.drawable.ic_horror),
//    MUSIC(10402, "Music", R.drawable.ic_music),
//    MYSTERY(9648, "Mystery", R.drawable.ic_mystery),
//    ROMANCE(10749, "Romance", R.drawable.ic_romance),
//    SCIENCE_FICTION(878, "SCIENCE FICTION", R.drawable.ic_science_fiction),
//    THRILLER(53, "Thriller", R.drawable.ic_thriller),
//    WAR(10752, "War", R.drawable.ic_war)
}