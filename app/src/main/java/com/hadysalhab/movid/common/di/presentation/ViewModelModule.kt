package com.hadysalhab.movid.common.di.presentation

import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.screen.main.featured.FeaturedViewModel
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailViewModel
import com.hadysalhab.movid.screen.main.movielist.MovieListViewModel
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
    abstract fun movieDetailViewModel(myViewModel2: MovieDetailViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(MovieListViewModel::class)
    abstract fun movieListViewModel(myViewModel2: MovieListViewModel): ViewModel
}