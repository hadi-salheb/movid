package com.hadysalhab.movid.screen.main.account

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.ViewFactory

class AccountViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : AccountView() {
    init {
        setRootView(layoutInflater.inflate(R.layout.layout_account, parent, false))
    }
}