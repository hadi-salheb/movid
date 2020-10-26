package com.hadysalhab.movid.screen.common.dialogs

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.hadysalhab.movid.screen.common.dialogs.infodialog.InfoDialogFragment
import com.hadysalhab.movid.screen.common.dialogs.ratedialog.RateDialogFragment

class DialogManager(private val context: Context, private val fragmentManager: FragmentManager) {

    fun showSignUpInfoDialog(tag: String?) {
        val dialogFragment = InfoDialogFragment.newInstance(
            title = "Note",
            message = "Please note that this app uses the TMDb API but is not endorsed or certified by TMDb.\nAfter sign up, please return to the Movid app to login using your credentials.",
            caption = "Ok"
        )
        dialogFragment.show(fragmentManager, tag)
    }

    fun showRateDialog(tag: String?, movieTitle: String, currentRating: Double?, movieId: Int) {
        val rateDialog = RateDialogFragment.newInstance(
            movieTitle = movieTitle,
            currentRating = currentRating,
            movieId = movieId
        )
        rateDialog.show(fragmentManager, tag)
    }
}