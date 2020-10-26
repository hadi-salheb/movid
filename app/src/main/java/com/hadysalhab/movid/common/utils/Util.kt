package com.hadysalhab.movid.common.utils

import android.content.Context
import android.util.DisplayMetrics
import com.hadysalhab.movid.movies.VideosResponse

fun convertDpToPixel(dp: Int, context: Context): Int {
    return dp * (context.resources
        .displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun getYoutubeTrailerFromResponse(videoResponse: VideosResponse) =
    videoResponse.videos.find {
        it.site == "YouTube" && it.type == "Trailer"
    }

fun getVoteAverageNumbers(): List<String> {
    var number: Float = 0.0f
    val numbers: MutableList<String> = mutableListOf()
    for (i in 0..20) {
        numbers.add((i / 2.0).toString())
    }
    return numbers
}


fun getRatingOptions(): List<Double> {
    val options = mutableListOf<Double>()
    for (i in 1..20) {
        options.add((i / 2.0))
    }
    return options
}