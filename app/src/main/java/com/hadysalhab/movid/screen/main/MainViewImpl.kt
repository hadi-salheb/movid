package com.hadysalhab.movid.screen.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hadysalhab.movid.R
import java.lang.RuntimeException

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
            var bottomNavigationItem: BottomNavigationItems = when (item.itemId) {
                R.id.bottom_nav_featured -> {
                    BottomNavigationItems.FEATURED
                }
                R.id.bottom_nav_account -> {
                    BottomNavigationItems.ACCOUNT
                }
                R.id.bottom_nav_favorites -> {
                    BottomNavigationItems.FAVORITES
                }
                R.id.bottom_nav_search -> {
                    BottomNavigationItems.SEARCH
                }
                R.id.bottom_nav_wishList -> {
                    BottomNavigationItems.WISHLIST
                }
                else -> {
                    throw RuntimeException("BottomNavigationItem id ${item.itemId} not supported")
                }
            }
            listeners.forEach { listener ->
                listener.onBottomNavigationItemClicked(bottomNavigationItem)
            }
            true
        }
    }

    override fun getFragmentFrame() = fragmentFrame
    override fun getCurrentNavigationItem(): BottomNavigationItems =
        when (bottomNavigation.selectedItemId) {
            R.id.bottom_nav_featured -> {
                BottomNavigationItems.FEATURED
            }
            R.id.bottom_nav_account -> {
                BottomNavigationItems.ACCOUNT
            }
            R.id.bottom_nav_favorites -> {
                BottomNavigationItems.FAVORITES
            }
            R.id.bottom_nav_search -> {
                BottomNavigationItems.SEARCH
            }
            R.id.bottom_nav_wishList -> {
                BottomNavigationItems.WISHLIST
            }
            else -> {
                throw RuntimeException("BottomNavigationItem id ${bottomNavigation.selectedItemId} not supported")
            }
        }
}