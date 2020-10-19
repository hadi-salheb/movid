package com.hadysalhab.movid.screen.main.account

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
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

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_account, parent, false))
        profileImageView = findViewById(R.id.profile_image)
        toolbar = findViewById(R.id.toolbar)
        menuToolbarLayout = viewFactory.getMenuToolbarLayout(toolbar)
        toolbar.addView(menuToolbarLayout.getRootView())
        menuToolbarLayout.setToolbarTitle("ACCOUNT")
    }

    override fun handleState(state: AccountViewState) {
        state.accountResponse?.let { accountResponse ->
            val drawable = TextDrawable.builder().buildRound(
                accountResponse.username[0].toString(),
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)
            )
            profileImageView.setImageDrawable(drawable)
        }
    }
}