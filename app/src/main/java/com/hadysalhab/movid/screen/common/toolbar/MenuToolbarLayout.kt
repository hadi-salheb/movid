package com.hadysalhab.movid.screen.common.toolbar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

class MenuToolbarLayout(layoutInflater: LayoutInflater, parent: ViewGroup?) :
    BaseObservableViewMvc<MenuToolbarLayout.Listener>() {
    interface Listener {
        fun onOverflowMenuIconClick()
    }

    private val overflowMenuIcon: ImageView
    private val menuToolbarTextView: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_menu_toolbar, parent, false))
        overflowMenuIcon = findViewById(R.id.overflow_menu_icon)
        menuToolbarTextView = findViewById(R.id.menu_toolbar_title)
        overflowMenuIcon.setOnClickListener {
            listeners.forEach {
                it.onOverflowMenuIconClick()
            }
        }
    }


    fun getOverflowMenuIconPlaceHolder() = overflowMenuIcon

    fun setOverflowMenuIcon(@DrawableRes icon: Int?) {
        if (icon == null) {
            overflowMenuIcon.visibility = View.GONE
        } else {
            overflowMenuIcon.setImageResource(icon)
            overflowMenuIcon.visibility = View.VISIBLE
        }
    }

    fun setToolbarTitle(title: String) {
        menuToolbarTextView.text = title
    }
}