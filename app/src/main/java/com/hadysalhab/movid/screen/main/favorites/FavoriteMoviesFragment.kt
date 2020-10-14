package com.hadysalhab.movid.screen.main.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitle
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleState
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

class FavoriteMoviesFragment : BaseFragment(), ListWithToolbarTitle.Listener {
    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var mainNavigator: MainNavigator

    private lateinit var listWithToolbarTitle: ListWithToolbarTitle

    private lateinit var favoriteMoviesViewModel: FavoriteMoviesViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        injector.inject(this)

        favoriteMoviesViewModel =
            ViewModelProvider(this, myViewModelFactory).get(FavoriteMoviesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::listWithToolbarTitle.isInitialized) {
            listWithToolbarTitle = viewFactory.getListWithToolbarTitle(container)
        }
        // Inflate the layout for this fragment
        return listWithToolbarTitle.getRootView()
    }

    override fun onStart() {
        super.onStart()
        favoriteMoviesViewModel.onStart()
        registerObservers()
    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FavoriteMoviesFragment()
    }

    //----------------------------------------------------------------------------------------------
    override fun onMovieItemClicked(movieID: Int) {
        mainNavigator.toDetailFragment(movieID)
    }

    override fun loadMoreItems() {
        favoriteMoviesViewModel.loadMore()
    }

    override fun onRetryClicked() {
        favoriteMoviesViewModel.onRetry()
    }

    override fun onPaginationErrorClicked() {
        favoriteMoviesViewModel.loadMore()
    }

    override fun onMenuIconClicked() {

    }

    //----------------------------------------------------------------------------------------------

    private val listWithToolbarTitleStateObserver =
        Observer<ListWithToolbarTitleState> { listWithToolbarTitleState ->
            listWithToolbarTitle.handleScreenState(listWithToolbarTitleState)
        }


    private fun registerObservers() {
        favoriteMoviesViewModel.state.observeForever(listWithToolbarTitleStateObserver)
        listWithToolbarTitle.registerListener(this)
    }

    private fun unregisterObservers() {
        favoriteMoviesViewModel.state.removeObserver(listWithToolbarTitleStateObserver)
        listWithToolbarTitle.unregisterListener(this)
    }
}