package com.hadysalhab.movid.screen.common.dialogs

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.hadysalhab.movid.screen.common.dialogs.infodialog.InfoDialogFragment

class DialogManager(private val context: Context, private val fragmentManager: FragmentManager) {

    fun showSignUpInfoDialog(tag: String?) {
        val dialogFragment = InfoDialogFragment.newInstance(
            title = "Note",
            message = "Please note that this app uses the TMDb API but is not endorsed or certified by TMDb.\nAfter sign up, please return to the Movid app to login using your credentials.",
            caption = "Ok"
        )
        dialogFragment.show(fragmentManager, tag)
    }
}