package com.hadysalhab.movid.screen.main.featured

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem

class FeaturedViewImpl(
    inflater: LayoutInflater,
    parent: ViewGroup?,
    private val viewFactory: ViewFactory
) :
    FeaturedView(), MovieGroupAdapter.Listener, MenuToolbarLayout.Listener {
    private val recyclerView: RecyclerView
    private val adapter: MovieGroupAdapter
    private val circularProgress: ProgressBar
    private val toolbar: Toolbar
    private val powerMenu: PowerMenu
    private val menuToolbarLayout: MenuToolbarLayout

    init {
        setRootView(inflater.inflate(R.layout.layout_featured, parent, false))
        recyclerView = findViewById(R.id.rv_movies)
        adapter = MovieGroupAdapter(this, viewFactory)
        circularProgress = findViewById(R.id.progress_circular)
        toolbar = findViewById(R.id.toolbar)
        powerMenu = getPowerMenu()
        menuToolbarLayout = getMenuToolbarLayout()
        setupRecyclerView()
        setUpToolbar()
    }

    private fun getPowerMenu() = PowerMenu.Builder(getContext())
        .addItemList(
            ToolbarCountryItems.values().toMutableList().mapIndexed { index, toolbarCountryItem ->
                PowerMenuItem(
                    toolbarCountryItem.countryName,
                    toolbarCountryItem.countryIcon,
                    index == 0
                )
            })
        .setMenuRadius(10f) // sets the corner radius.
        .setTextSize(16)
        .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) // Animation start point (TOP | LEFT).
        .setMenuColor(Color.WHITE)
        .setOnMenuItemClickListener { position, item ->
            powerMenu.selectedPosition = position
            menuToolbarLayout.setOverflowMenuIcon(item.icon)
            powerMenu.dismiss()
        }
        .build()

    private fun getMenuToolbarLayout() = viewFactory.getMenuToolbarLayout(toolbar)

    private fun setUpToolbar() {
        menuToolbarLayout.setOverflowMenuIcon(ToolbarCountryItems.AUSTRALIA.countryIcon)
        toolbar.addView(menuToolbarLayout.getRootView())
    }


    private fun setupRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = this@FeaturedViewImpl.adapter
        }
    }

    override fun onMovieCardClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieCardClicked(movieID)
        }
    }

    override fun onSeeMoreClicked(groupType: GroupType) {
        listeners.forEach {
            it.onSeeAllClicked(groupType)
        }
    }

    override fun displayMovieGroups(movieGroups: List<MoviesResponse>) {
        circularProgress.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        // if adapter already contains a list
        // diffUtil will render only items that are different
        adapter.submitList(movieGroups)
        menuToolbarLayout.registerListener(this)
    }


    override fun displayLoadingScreen() {
        menuToolbarLayout.unregisterListener(this)
        circularProgress.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    override fun onOverflowMenuIconClick() {
        powerMenu.showAsAnchorRightTop(menuToolbarLayout.getOverflowMenuIconPlaceHolder())
    }

}