package com.hadysalhab.movid.screen.main.account

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.amulyakhare.textdrawable.TextDrawable
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout

class AccountViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : AccountView() {
    private val profileImageView: ImageView
    private val toolbar: Toolbar
    private val menuToolbarLayout: MenuToolbarLayout
    private val usernameTextView: TextView
    private val signOutButton: Button
    private val aboutLL: LinearLayout
    private val contactDeveloperLL: LinearLayout
    private val librariesLL: LinearLayout

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_account, parent, false))
        profileImageView = findViewById(R.id.profile_image)
        toolbar = findViewById(R.id.toolbar)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar)
        toolbar.addView(menuToolbarLayout.getRootView())
        menuToolbarLayout.setToolbarTitle("ACCOUNT")
        usernameTextView = findViewById(R.id.account_username)

        signOutButton = findViewById(R.id.signOut_btn)
        signOutButton.setOnClickListener {
            listeners.forEach {
                it.onSignOutClick()
            }
        }
        aboutLL = findViewById(R.id.about_linearLayout)
        aboutLL.setOnClickListener {
            listeners.forEach {
                it.onAboutClick()
            }
        }
        contactDeveloperLL = findViewById(R.id.contact_developer)
        contactDeveloperLL.setOnClickListener {
            listeners.forEach {
                it.onContactDevClicked()
            }
        }
        librariesLL = findViewById(R.id.libraries)
        librariesLL.setOnClickListener {
            listeners.forEach {
                it.onLibrariesClicked()
            }
        }
    }

    override fun handleState(state: AccountViewState) {
        state.accountResponse?.let { accountResponse ->
            val drawable = TextDrawable.builder().buildRound(
                accountResponse.username[0].toString(),
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)
            )
            profileImageView.setImageDrawable(drawable)

            usernameTextView.text = accountResponse.username
        }
    }
}