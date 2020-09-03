package com.hadysalhab.movid.common.di.presentation

import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.screen.main.favorites.FavoriteMoviesViewModel
import com.hadysalhab.movid.screen.main.featured.FeaturedViewModel
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailViewModel
import com.hadysalhab.movid.screen.main.movielist.MovieListViewModel
import com.hadysalhab.movid.screen.main.reviews.ReviewsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(FeaturedViewModel::class)
    abstract fun featuredViewModel(myViewModel: FeaturedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailViewModel::class)
    abstract fun movieDetailViewModel(myViewModel: MovieDetailViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(MovieListViewModel::class)
    abstract fun movieListViewModel(myViewModel: MovieListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReviewsViewModel::class)
    abstract fun reviewsViewModel(myViewModel: ReviewsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteMoviesViewModel::class)
    abstract fun favoriteMoviesViewModel(myViewModel: FavoriteMoviesViewModel): ViewModel
}