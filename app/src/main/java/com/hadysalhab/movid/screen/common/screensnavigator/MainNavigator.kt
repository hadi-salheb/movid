package com.hadysalhab.movid.screen.common.screensnavigator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.screen.authentication.AuthActivity
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost
import com.hadysalhab.movid.screen.common.people.PeopleType
import com.hadysalhab.movid.screen.main.BottomNavigationItems
import com.hadysalhab.movid.screen.main.about.AboutFragment
import com.hadysalhab.movid.screen.main.account.AccountFragment
import com.hadysalhab.movid.screen.main.castlist.PeopleListFragment
import com.hadysalhab.movid.screen.main.discover.DiscoverFragment
import com.hadysalhab.movid.screen.main.favorites.FavoriteMoviesFragment
import com.hadysalhab.movid.screen.main.featuredgroups.FeaturedGroupFragment
import com.hadysalhab.movid.screen.main.featuredlist.FeaturedListFragment
import com.hadysalhab.movid.screen.main.filter.FilterFragment
import com.hadysalhab.movid.screen.main.libraries.LibrariesFragment
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailFragment
import com.hadysalhab.movid.screen.main.privacypolicy.PrivacyPolicyFragment
import com.hadysalhab.movid.screen.main.recommendedsimilar.RecommendedSimilarFragment
import com.hadysalhab.movid.screen.main.reviews.ReviewsFragment
import com.hadysalhab.movid.screen.main.search.Genre
import com.hadysalhab.movid.screen.main.search.SearchFragment
import com.hadysalhab.movid.screen.main.watchlist.WatchlistMoviesFragment
import com.hadysalhab.movid.screen.splash.SplashActivity
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
                FragNavController.TAB1 -> FeaturedGroupFragment.newInstance()
                FragNavController.TAB2 -> SearchFragment.newInstance()
                FragNavController.TAB3 -> FavoriteMoviesFragment.newInstance()
                FragNavController.TAB4 -> WatchlistMoviesFragment.newInstance()
                FragNavController.TAB5 -> AccountFragment.newInstance()
                else -> throw IllegalStateException("unsupported tab index: $index")
            }
        }
    }


    fun init(
        savedInstanceState: Bundle?,
        fragTransactionListener: FragNavController.TransactionListener
    ) {
        fragNavController =
            FragNavController(fragmentManager, fragmentFrameHost.getFragmentFrame().id)
        fragNavController.apply {
            rootFragmentListener = this@MainNavigator.rootFragmentListener
            transactionListener = fragTransactionListener
        }
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

    fun toFeaturedListFragment(groupType: GroupType, region: String) =
        fragNavController.pushFragment(FeaturedListFragment.newInstance(groupType, region))

    fun toReviewsFragment(movieID: Int, movieName: String) {
        fragNavController.pushFragment(ReviewsFragment.newInstance(movieID, movieName))
    }

    fun switchTab(bottomNavigationItems: BottomNavigationItems) {
        val tabIndex: Int = when (bottomNavigationItems) {
            BottomNavigationItems.FEATURED -> FragNavController.TAB1
            BottomNavigationItems.SEARCH -> FragNavController.TAB2
            BottomNavigationItems.FAVORITES -> FragNavController.TAB3
            BottomNavigationItems.WATCHLIST -> FragNavController.TAB4
            BottomNavigationItems.ACCOUNT -> FragNavController.TAB5
        }
        fragNavController.switchTab(tabIndex)
    }

    fun clearCurrentStack() {
        fragNavController.clearStack()
    }

    fun toDiscoverFragment(genre: Genre) =
        fragNavController.pushFragment(DiscoverFragment.newInstance(genre))

    fun toRecommendedSimilarMoviesFragment(groupType: GroupType, movieName: String, movieID: Int) {
        fragNavController.pushFragment(
            RecommendedSimilarFragment.newInstance(
                groupType, movieID, movieName
            )
        )
    }

    fun toFilterFragment() {
        fragNavController.pushFragment(
            FilterFragment.newInstance()
        )
    }

    fun popFragment() {
        fragNavController.popFragment()
    }

    fun toAuthActivity() {
        val intent = Intent(context, AuthActivity::class.java)
        context.startActivity(intent)
    }

    fun toAboutFragment() {
        fragNavController.pushFragment(
            AboutFragment.newInstance()
        )
    }

    fun toPeopleListFragment(movieID: Int, movieName: String, peopleType: PeopleType) {
        fragNavController.pushFragment(
            PeopleListFragment.newInstance(movieID, movieName, peopleType)
        )
    }

    fun toSplashActivity() {
        val intent = Intent(context, SplashActivity::class.java)
        context.startActivity(intent)
    }

    fun toLibrariesFragment() {
        fragNavController.pushFragment(
            LibrariesFragment.newInstance()
        )
    }

    fun toPrivacyPolicyFragment() {
        fragNavController.pushFragment(
            PrivacyPolicyFragment.newInstance()
        )
    }

}