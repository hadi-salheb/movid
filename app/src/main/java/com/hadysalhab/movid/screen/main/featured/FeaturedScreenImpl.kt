package com.hadysalhab.movid.screen.main.featured

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.errorscreen.ErrorScreen
import com.hadysalhab.movid.screen.common.toolbar.MenuToolbarLayout
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem

class FeaturedScreenImpl(
    inflater: LayoutInflater,
    parent: ViewGroup?,
    private val viewFactory: ViewFactory
) :
    FeaturedScreen(), MovieGroupAdapter.Listener, MenuToolbarLayout.Listener, ErrorScreen.Listener,
    SwipeRefreshLayout.OnRefreshListener {
    private val recyclerView: RecyclerView
    private val adapter: MovieGroupAdapter
    private val circularProgress: ProgressBar
    private val toolbar: Toolbar
    private val powerMenu: PowerMenu
    private val menuToolbarLayout: MenuToolbarLayout
    private val errorScreen: ErrorScreen
    private val errorScreenPlaceHolder: FrameLayout
    private val pullToRefresh: SwipeRefreshLayout

    init {
        setRootView(inflater.inflate(R.layout.layout_featured, parent, false))
        recyclerView = findViewById(R.id.rv_movies)
        adapter = MovieGroupAdapter(this, viewFactory)
        circularProgress = findViewById(R.id.progress_circular)
        toolbar = findViewById(R.id.toolbar)
        powerMenu = getPowerMenu()
        errorScreenPlaceHolder = findViewById(R.id.error_screen_placeholder)
        errorScreen = viewFactory.getErrorScreen(errorScreenPlaceHolder)
        errorScreen.registerListener(this)
        errorScreenPlaceHolder.addView(errorScreen.getRootView())
        menuToolbarLayout = getMenuToolbarLayout()
        menuToolbarLayout.registerListener(this)
        pullToRefresh = findViewById(R.id.pull_to_refresh)
        pullToRefresh.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.teal_600))
        pullToRefresh.setOnRefreshListener(this)
        setupRecyclerView()
        setUpToolbar()
    }

    private fun getPowerMenu() = PowerMenu.Builder(getContext())
        .addItemList(
            ToolbarCountryItems.values().toMutableList().mapIndexed { index, toolbarCountryItem ->
                PowerMenuItem(
                    toolbarCountryItem.countryName,
                    toolbarCountryItem.countryIcon
                )
            })
        .setMenuRadius(10f) // sets the corner radius.
        .setTextSize(16)
        .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) // Animation start point (TOP | LEFT).
        .setMenuColor(Color.WHITE)
        .setDismissIfShowAgain(false)
        .setOnBackgroundClickListener {
            listeners.forEach {
                it.onBackgroundClick()
            }
        }
        .setAutoDismiss(false)
        .setFocusable(true)
        .setOnMenuItemClickListener { position, item ->
            if (powerMenu.selectedPosition == position) {
                return@setOnMenuItemClickListener
            }
            listeners.forEach {
                it.onCountryToolbarItemClicked(ToolbarCountryItems.values()[position])
            }
        }
        .build()

    private fun getMenuToolbarLayout() = viewFactory.getMenuToolbarLayout(toolbar)

    private fun setUpToolbar() {
        toolbar.addView(menuToolbarLayout.getRootView())
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = this@FeaturedScreenImpl.adapter
        }
    }


    override fun handleState(featuredState: FeaturedScreenState) {
        val sortedMovies = sortMoviesAndReturn(featuredState.data)
        displayMovieGroups(sortedMovies)
        if (featuredState.isLoading) {
            showLoadingIndicator()
        } else {
            hideLoadingIndicator()
        }

        if (featuredState.errorMessage != null) {
            showErrorScreen(featuredState.errorMessage)
        } else hideErrorScreen()
        setPowerMenuItem(featuredState.powerMenuItem)
        if (featuredState.isPowerMenuOpen) showPowerMenu() else hidePowerMenu()
        if (featuredState.isRefreshing) {
            showRefreshIndicator()
        } else {
            hideRefreshIndicator()
        }
        if (!featuredState.isRefreshing) {
            if (featuredState.isLoading || featuredState.errorMessage != null) {
                disablePullToRefresh()
            } else {
                enablePullToRefresh()
            }
        }

    }

    private fun sortMoviesAndReturn(movieGroups: List<MoviesResponse>) =
        movieGroups.sortedBy { item -> item.tag.ordinal }
            .filter { !it.movies.isNullOrEmpty() }

    private fun displayMovieGroups(movieGroups: List<MoviesResponse>) {
        adapter.submitList(movieGroups)
    }

    private fun showLoadingIndicator() {
        circularProgress.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        circularProgress.visibility = View.GONE
    }

    private fun showPowerMenu() {
        powerMenu.showAsAnchorRightTop(menuToolbarLayout.getOverflowMenuIconPlaceHolder())
    }

    private fun hidePowerMenu() {
        powerMenu.dismiss()
    }

    private fun setPowerMenuItem(powerMenuItem: ToolbarCountryItems) {
        powerMenu.selectedPosition = powerMenuItem.ordinal
        menuToolbarLayout.setOverflowMenuIcon(powerMenuItem.countryIcon)
    }

    private fun showErrorScreen(errorMessage: String) {
        errorScreen.displayErrorMessage(errorMessage)
        errorScreenPlaceHolder.visibility = View.VISIBLE
    }

    private fun hideErrorScreen() {
        errorScreenPlaceHolder.visibility = View.GONE
    }

    private fun hideRefreshIndicator() {
        pullToRefresh.isRefreshing = false
    }

    private fun showRefreshIndicator() {
        pullToRefresh.isRefreshing = true
    }

    private fun enablePullToRefresh() {
        pullToRefresh.isEnabled = true
    }

    private fun disablePullToRefresh() {
        pullToRefresh.isEnabled = false
    }


    override fun onOverflowMenuIconClick() {
        listeners.forEach {
            it.onOverflowMenuIconClick()
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

    override fun onRetryClicked() {
        listeners.forEach { it.onRetryClicked() }
    }

    override fun onRefresh() {
        listeners.forEach {
            it.onRefresh()
        }
    }

}