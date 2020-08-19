package com.hadysalhab.movid.common.di.presentation

import androidx.lifecycle.ViewModel
import com.hadysalhab.movid.screen.main.featured.FeaturedViewModel
import com.hadysalhab.movid.screen.main.moviedetail.MovieDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(FeaturedViewModel::class)
    abstract fun myViewModel(myViewModel: FeaturedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailViewModel::class)
    abstract fun myViewModel2(myViewModel2: MovieDetailViewModel): ViewModel

}