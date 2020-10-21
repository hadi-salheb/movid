package com.hadysalhab.movid.screen.common.intenthandler

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.hadysalhab.movid.common.constants.YOUTUBE_BASE_URL
import com.hadysalhab.movid.common.utils.getYoutubeTrailerFromResponse
import com.hadysalhab.movid.movies.VideosResponse

class IntentHandler(private val activityContext: Context) {


    fun handleTrailerIntent(videoResponse: VideosResponse) {
        val video = getYoutubeTrailerFromResponse(videoResponse)
            ?: throw RuntimeException("Cannot handle trailer intent!!") //this flow should not be reached if intent cannot handle trailer
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

    fun handleSignUpIntent() {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themoviedb.org/signup"))
        activityContext.startActivity(browserIntent)
    }

}