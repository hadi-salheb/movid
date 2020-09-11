package com.hadysalhab.movid.screen.common.toolbar

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

class MenuToolbarLayout(layoutInflater: LayoutInflater, parent: ViewGroup?) :
    BaseObservableViewMvc<MenuToolbarLayout.Listener>() {
    interface Listener {
        fun onOverflowMenuIconClick()
    }

    private val overflowMenuIcon: ImageView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_menu_toolbar, parent, false))
        overflowMenuIcon = findViewById(R.id.overflow_menu_icon)
        overflowMenuIcon.setOnClickListener {
            listeners.forEach {
                it.onOverflowMenuIconClick()
            }
        }
    }


    fun getOverflowMenuIconPlaceHolder() = overflowMenuIcon

    fun setOverflowMenuIcon(@DrawableRes icon: Int) {
        overflowMenuIcon.setImageResource(icon)
    }
}