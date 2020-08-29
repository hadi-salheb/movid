package com.hadysalhab.movid.screen.main

import android.widget.FrameLayout
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class MainView : BaseObservableViewMvc<MainView.Listener>() {
    interface Listener {
        fun onBottomNavigationItemClicked(item: BottomNavigationItems)
    }

    abstract fun getFragmentFrame(): FrameLayout
    abstract fun getCurrentNavigationItem(): BottomNavigationItems
}