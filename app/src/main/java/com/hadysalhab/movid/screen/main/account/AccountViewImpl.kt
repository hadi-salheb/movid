package com.hadysalhab.movid.screen.main.account

import android.content.res.TypedArray
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.amulyakhare.textdrawable.TextDrawable
import com.google.android.material.switchmaterial.SwitchMaterial
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
    private val shareLL: LinearLayout
    private val rateLL: LinearLayout
    private val darkModeSwitch: SwitchMaterial

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

        shareLL = findViewById(R.id.share_ll)
        shareLL.setOnClickListener {
            listeners.forEach {
                it.onShareClicked()
            }
        }

        rateLL = findViewById(R.id.rate_ll)
        rateLL.setOnClickListener {
            listeners.forEach {
                it.onRateClicked()
            }
        }

        darkModeSwitch = findViewById(R.id.dark_mode_switch)
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            listeners.forEach {
                it.onDarkModeCheckedChanged(isChecked)
            }
        }
    }

    override fun handleState(state: AccountViewState) {
        if (state.accountResponse != null) {
            val typedValue = TypedValue()

            val a: TypedArray =
                getContext().obtainStyledAttributes(
                    typedValue.data,
                    intArrayOf(R.attr.colorSecondaryVariant)
                )
            val color: Int = a.getColor(0, 0)

            a.recycle()
            val drawable = TextDrawable.builder().buildRound(
                state.accountResponse.username[0].toString(),
                color
            )
            profileImageView.setImageDrawable(drawable)
            usernameTextView.text = state.accountResponse.username
            signOutButton.text = "Sign out"
        } else {
            profileImageView.setImageResource(R.drawable.user_default_profile)
            usernameTextView.text = ""
            signOutButton.text = "LOGIN"
        }

    }

    override fun toggleDarkModeSwitch(darkTheme: Boolean) {
        darkModeSwitch.isChecked = darkTheme
    }
}