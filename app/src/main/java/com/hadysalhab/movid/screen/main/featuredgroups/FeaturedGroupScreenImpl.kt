package com.hadysalhab.movid.screen.main.featuredgroups

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

class FeaturedGroupScreenImpl(
    inflater: LayoutInflater,
    parent: ViewGroup?,
    private val viewFactory: ViewFactory
) :
    FeaturedGroupScreen(), FeaturedGroupAdapter.Listener, MenuToolbarLayout.Listener,
    ErrorScreen.Listener,
    SwipeRefreshLayout.OnRefreshListener {
    private val recyclerView: RecyclerView
    private val adapter: FeaturedGroupAdapter
    private val circularProgress: ProgressBar
    private val toolbar: Toolbar
    private val powerMenu: PowerMenu
    private val menuToolbarLayout: MenuToolbarLayout
    private val errorScreen: ErrorScreen
    private val errorScreenPlaceHolder: FrameLayout
    private val pullToRefresh: SwipeRefreshLayout

    init {
        setRootView(inflater.inflate(R.layout.layout_featured, parent, false))
        recyclerView = findViewById(R.id.rv_data)
        adapter = FeaturedGroupAdapter(this, viewFactory)
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
            ToolbarCountryItems.values().toMutableList().mapIndexed { _, toolbarCountryItem ->
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
        .setOnMenuItemClickListener { position, _ ->
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
            adapter = this@FeaturedGroupScreenImpl.adapter
        }
    }

    override fun displayFeaturedMovies(featured: List<MoviesResponse>) {
        adapter.submitList(featured)
    }

    override fun showLoadingIndicator() {
        circularProgress.visibility = View.VISIBLE
    }

    override fun hideLoadingIndicator() {
        circularProgress.visibility = View.GONE
    }

    override fun showPowerMenu() {
        powerMenu.showAsAnchorRightTop(menuToolbarLayout.getOverflowMenuIconPlaceHolder())
    }

    override fun hidePowerMenu() {
        powerMenu.dismiss()
    }

    override fun setPowerMenuItem(toolbarCountryItem: ToolbarCountryItems) {
        powerMenu.selectedPosition = toolbarCountryItem.ordinal
        menuToolbarLayout.setOverflowMenuIcon(toolbarCountryItem.countryIcon)
    }

    override fun disablePullRefresh() {
        pullToRefresh.isEnabled = false
    }

    override fun enablePullRefresh() {
        pullToRefresh.isEnabled = true
    }

    override fun showErrorScreen(error: String) {
        errorScreen.displayErrorMessage(error)
        errorScreenPlaceHolder.visibility = View.VISIBLE
    }

    override fun hideErrorScreen() {
        errorScreenPlaceHolder.visibility = View.GONE
    }

    override fun hideRefreshIndicator() {
        pullToRefresh.isRefreshing = false
    }

    override fun showRefreshIndicator() {
        pullToRefresh.isRefreshing = true
    }

    override fun onOverflowMenuIconClick() {
        listeners.forEach {
            it.onOverflowMenuIconClick()
        }
    }

    override fun onBackArrowClicked() {

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

    override fun handleScreenState(featuredScreenState: FeaturedScreenState) {
        val sortedMovies = sortMoviesAndReturn(featuredScreenState.data)
        displayFeaturedMovies(sortedMovies)
        if (featuredScreenState.isLoading) {
            showLoadingIndicator()
        } else {
            hideLoadingIndicator()
        }

        if (featuredScreenState.errorMessage != null) {
            showErrorScreen(featuredScreenState.errorMessage)
        } else hideErrorScreen()
        setPowerMenuItem(featuredScreenState.powerMenuItem)
        if (featuredScreenState.isPowerMenuOpen) {
            showPowerMenu()
        } else {
            hidePowerMenu()
        }
        if (featuredScreenState.isRefreshing) {
            showRefreshIndicator()
        } else {
            hideRefreshIndicator()
        }
        if (!featuredScreenState.isRefreshing) {
            if (featuredScreenState.isLoading || featuredScreenState.errorMessage != null) {
                disablePullRefresh()
            } else {
                enablePullRefresh()
            }
        }
    }

    private fun sortMoviesAndReturn(movieGroups: List<MoviesResponse>) =
        movieGroups.sortedBy { item -> item.tag.ordinal }
            .filter { !it.movies.isNullOrEmpty() }

}