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