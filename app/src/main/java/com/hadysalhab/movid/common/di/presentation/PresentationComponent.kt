package com.hadysalhab.movid.common.di.presentation

import com.android.roam.wheelycool.dependencyinjection.presentation.PresentationModule
import com.android.roam.wheelycool.dependencyinjection.presentation.PresentationScope
import com.hadysalhab.movid.screen.authentication.AuthActivity
import com.hadysalhab.movid.screen.common.dialogs.infodialog.InfoDialogFragment
import com.hadysalhab.movid.screen.common.dialogs.ratedialog.RateDialogFragment
import com.hadysalhab.movid.screen.main.MainActivity
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
import com.hadysalhab.movid.screen.main.search.SearchFragment
import com.hadysalhab.movid.screen.main.watchlist.WatchlistMoviesFragment
import com.hadysalhab.movid.screen.splash.SplashActivity
import dagger.Subcomponent

/**
 * Presentation-Level component
 */
@PresentationScope
@Subcomponent(modules = [PresentationModule::class, ViewModelModule::class])
interface PresentationComponent {
    fun inject(authActivity: AuthActivity)
    fun inject(featuredGroupFragment: FeaturedGroupFragment)
    fun inject(mainActivity: MainActivity)
    fun inject(splashActivity: SplashActivity)
    fun inject(movieDetailFragment: MovieDetailFragment)
    fun inject(featuredListFragment: FeaturedListFragment)
    fun inject(reviewsFragment: ReviewsFragment)
    fun inject(favoriteMoviesFragment: FavoriteMoviesFragment)
    fun inject(watchlistMoviesFragment: WatchlistMoviesFragment)
    fun inject(searchFragment: SearchFragment)
    fun inject(discoverFragment: DiscoverFragment)
    fun inject(recommendedSimilarFragment: RecommendedSimilarFragment)
    fun inject(filterFragment: FilterFragment)
    fun inject(accountFragment: AccountFragment)
    fun inject(aboutFragment: AboutFragment)
    fun inject(peopleListFragment: PeopleListFragment)
    fun inject(librariesFragment: LibrariesFragment)
    fun inject(infoDialog: InfoDialogFragment)
    fun inject(rateDialogFragment: RateDialogFragment)
    fun inject(privacyPolicyFragment: PrivacyPolicyFragment)
}
