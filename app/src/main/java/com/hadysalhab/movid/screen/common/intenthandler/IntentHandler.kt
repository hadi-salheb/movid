package com.hadysalhab.movid.screen.common.intenthandler

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.hadysalhab.movid.BuildConfig
import com.hadysalhab.movid.common.constants.YOUTUBE_BASE_URL
import com.hadysalhab.movid.common.utils.getYoutubeTrailerFromResponse
import com.hadysalhab.movid.movies.VideosResponse

class IntentHandler(private val activityContext: Context) {


    fun handleTrailerIntent(videoResponse: VideosResponse) {
        val video = getYoutubeTrailerFromResponse(videoResponse)
            ?: throw RuntimeException("Cannot handle trailer intent!!") //this flow should not be reached if intent cannot handle trailer
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${video.key}"))
        if (isActivityAvailable(appIntent)) {
            activityContext.startActivity(appIntent)
        } else {
            val webIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + video.key))
            if (isActivityAvailable(webIntent)) {
                activityContext.startActivity(webIntent)
            }
        }
    }

    fun handleSignUpIntent() {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themoviedb.org/signup"))
        if (isActivityAvailable(browserIntent)) {
            activityContext.startActivity(browserIntent)
        }
    }

    fun handleContactDev() {
        val emailIntent = Intent(Intent.ACTION_SEND).also {
            it.putExtra(Intent.EXTRA_EMAIL, arrayOf("hadisalheb@gmail.com"))
            it.putExtra(Intent.EXTRA_SUBJECT, "Movid Feedback")
            it.type = "message/rfc822"
        }
        if (isActivityAvailable(emailIntent)) {
            activityContext.startActivity(Intent.createChooser(emailIntent, "Send Email"))
        }
    }

    private fun isActivityAvailable(intent: Intent) =
        intent.resolveActivity(activityContext.packageManager) != null

    fun handleLibraryIntent(libraryUrl: String) {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(libraryUrl))
        if (isActivityAvailable(browserIntent)) {
            activityContext.startActivity(browserIntent)
        }
    }

    fun handleRateIntent() {
        val appStoreRateIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=${activityContext.packageName}")
        )
        if (isActivityAvailable(appStoreRateIntent)) {
            activityContext.startActivity(appStoreRateIntent)
        } else {
            val webStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=${activityContext.packageName}")
            )
            if (isActivityAvailable(webStoreIntent)) {
                activityContext.startActivity(webStoreIntent)
            }
        }
    }

    fun handleShareIntent() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Hey check out Movid app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            )
        }
        if (isActivityAvailable(shareIntent)) {
            activityContext.startActivity(Intent.createChooser(shareIntent, "Share Movid"))
        }
    }
}