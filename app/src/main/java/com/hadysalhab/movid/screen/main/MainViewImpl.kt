package com.hadysalhab.movid.screen.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hadysalhab.movid.R

class MainViewImpl(inflater: LayoutInflater, parent: ViewGroup?) : MainView() {
    private val fragmentFrame: FrameLayout
    private val bottomNavigation: BottomNavigationView

    init {
        setRootView(inflater.inflate(R.layout.layout_main, parent, false))
        fragmentFrame = findViewById(R.id.fragment_frame)
        bottomNavigation = findViewById(R.id.bottom_navigation_view)
        setUpViewListeners()
    }

    private fun setUpViewListeners() {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val bottomNavigationItem: BottomNavigationItems =
                getBottomNavigationItemFor(item.itemId)
            listeners.forEach { listener ->
                listener.onBottomNavigationItemClicked(bottomNavigationItem)
            }
            true
        }
    }

    override fun getFragmentFrame() = fragmentFrame
    override fun getCurrentNavigationItem(): BottomNavigationItems =
        getBottomNavigationItemFor(bottomNavigation.selectedItemId)

    private fun getBottomNavigationItemFor(id: Int) = when (id) {
        R.id.bottom_nav_featured -> {
            BottomNavigationItems.FEATURED
        }
        R.id.bottom_nav_search -> {
            BottomNavigationItems.SEARCH
        }
        R.id.bottom_nav_favorites -> {
            BottomNavigationItems.FAVORITES
        }

        R.id.bottom_nav_watchList -> {
            BottomNavigationItems.WATCHLIST
        }
        R.id.bottom_nav_account -> {
            BottomNavigationItems.ACCOUNT
        }
        else -> {
            throw RuntimeException("BottomNavigationItem id ${id} not supported")
        }
    }
}