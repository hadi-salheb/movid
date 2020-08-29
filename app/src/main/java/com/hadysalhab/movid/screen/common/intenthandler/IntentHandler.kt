package com.hadysalhab.movid.screen.common.intenthandler

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.hadysalhab.movid.common.constants.YOUTUBE_BASE_URL
import com.hadysalhab.movid.movies.VideosResponse

class IntentHandler(private val activityContext: Context) {
    fun handleTrailerIntent(videoResponse: VideosResponse) {
        val videos = videoResponse.videos
        val video = videos.find {
            it.site == "YouTube" && it.type == "Trailer"
        }
        video?.let {
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${video.key}"))
            if (appIntent.resolveActivity(activityContext.packageManager) != null) {
                activityContext.startActivity(appIntent)
            } else {
                val webIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + video.key))
                if (webIntent.resolveActivity(activityContext.packageManager) != null) {
                    activityContext.startActivity(webIntent)
                }
            }
        }

    }
}