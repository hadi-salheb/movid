package com.hadysalhab.movid.common.di.presentation

import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.screen.common.dialogs.ratedialog.RateDialogViewModel
import com.hadysalhab.movid.screen.main.account.AccountViewModel
import com.hadysalhab.movid.screen.main.castlist.PeopleListViewModel
import com.hadysalhab.movid.screen.main.discover.DiscoverViewModel
import com.hadysalhab.movid.screen.main.favorites.FavoriteMoviesViewModel
import com.hadysalhab.movid.screen.main.featuredgroups.FeaturedGroupViewModel
import com.hadysalhab.movid.screen.main.featuredlist.FeaturedListViewModel
import com.hadysalhab.movid.screen.main.filter.FilterViewModel
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailViewModel
import com.hadysalhab.movid.screen.main.recommendedsimilar.RecommendedSimilarViewModel
import com.hadysalhab.movid.screen.main.reviews.ReviewsViewModel
import com.hadysalhab.movid.screen.main.search.SearchViewModel
import com.hadysalhab.movid.screen.main.watchlist.WatchlistMoviesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(FeaturedGroupViewModel::class)
    abstract fun featuredViewModel(myGroupViewModel: FeaturedGroupViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailViewModel::class)
    abstract fun movieDetailViewModel(myViewModel: MovieDetailViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(FeaturedListViewModel::class)
    abstract fun movieListViewModel(myViewModel: FeaturedListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReviewsViewModel::class)
    abstract fun reviewsViewModel(myViewModel: ReviewsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteMoviesViewModel::class)
    abstract fun favoriteMoviesViewModel(myViewModel: FavoriteMoviesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WatchlistMoviesViewModel::class)
    abstract fun watchlistMoviesViewModel(myViewModel: WatchlistMoviesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun searchViewModel(myViewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DiscoverViewModel::class)
    abstract fun discoverViewModel(myViewModel: DiscoverViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RecommendedSimilarViewModel::class)
    abstract fun recommendedSimilarViewModel(myViewModel: RecommendedSimilarViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FilterViewModel::class)
    abstract fun filterViewModel(myViewModel: FilterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun accountViewModel(myViewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PeopleListViewModel::class)
    abstract fun castListViewModel(myViewModel: PeopleListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RateDialogViewModel::class)
    abstract fun rateDialogViewModel(myViewModel: RateDialogViewModel): ViewModel
}