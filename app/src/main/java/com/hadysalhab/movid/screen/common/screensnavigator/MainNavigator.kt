package com.hadysalhab.movid.screen.common.screensnavigator

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost
import com.hadysalhab.movid.screen.main.BottomNavigationItems
import com.hadysalhab.movid.screen.main.favorites.FavoriteMoviesFragment
import com.hadysalhab.movid.screen.main.featured.FeaturedFragment
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailFragment
import com.hadysalhab.movid.screen.main.movielist.MovieListFragment
import com.hadysalhab.movid.screen.main.reviews.ReviewsFragment
import com.hadysalhab.movid.screen.main.watchlist.WatchlistMoviesFragment
import com.ncapdevi.fragnav.FragNavController

class MainNavigator(
    private val fragmentManager: FragmentManager,
    private val fragmentFrameHost: FragmentFrameHost,
    private val context: Context
) {
    private val TAB_COUNT = 5

    private lateinit var fragNavController: FragNavController

    private val rootFragmentListener: FragNavController.RootFragmentListener = object :
        FragNavController.RootFragmentListener {
        override val numberOfRootFragments: Int
            get() = TAB_COUNT

        override fun getRootFragment(index: Int): Fragment {
            return when (index) {
                FragNavController.TAB1 -> FeaturedFragment.newInstance()
                FragNavController.TAB3 -> FavoriteMoviesFragment.newInstance()
                FragNavController.TAB4 -> WatchlistMoviesFragment.newInstance()
                else -> throw IllegalStateException("unsupported tab index: $index")
            }
        }
    }

    fun init(savedInstanceState: Bundle?) {
        fragNavController =
            FragNavController(fragmentManager, fragmentFrameHost.getFragmentFrame().id)
        fragNavController.rootFragmentListener = rootFragmentListener
        fragNavController.initialize(FragNavController.TAB1, savedInstanceState)
    }

    fun onSavedInstanceState(savedInstanceState: Bundle?) {
        fragNavController.onSaveInstanceState(savedInstanceState)
    }

    fun navigateUp() {
        fragNavController.popFragment()
    }

    fun isRootFragment(): Boolean = fragNavController.isRootFragment
    fun toDetailFragment(movieID: Int) =
        fragNavController.pushFragment(MovieDetailFragment.newInstance(movieID))

    fun toMovieListFragment(groupType: GroupType, movieID: Int?) =
        fragNavController.pushFragment(MovieListFragment.newInstance(groupType, movieID))

    fun clearFeaturedStack() {
        fragNavController.clearStack()
    }

    fun toReviewsFragment(movieID: Int) {
        fragNavController.pushFragment(ReviewsFragment.newInstance(movieID))
    }

    fun switchTab(bottomNavigationItems: BottomNavigationItems) {
        var tabIndex: Int = when (bottomNavigationItems) {
            BottomNavigationItems.FEATURED -> FragNavController.TAB1
            BottomNavigationItems.SEARCH -> FragNavController.TAB2
            BottomNavigationItems.FAVORITES -> FragNavController.TAB3
            BottomNavigationItems.WATCHLIST -> FragNavController.TAB4
            BottomNavigationItems.ACCOUNT -> FragNavController.TAB5
        }
        fragNavController.switchTab(tabIndex)
    }

}