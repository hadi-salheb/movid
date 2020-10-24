package com.hadysalhab.movid.screen.main.discover

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
import com.hadysalhab.movid.screen.main.search.Genre
import javax.inject.Inject

private const val ARG_GENRE_ID = "genre_id"

class DiscoverFragment : BaseFragment(), ListWithToolbarTitle.Listener {
    companion object {
        @JvmStatic
        fun newInstance(genre: Genre) =
            DiscoverFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_GENRE_ID, genre)
                }
            }
    }

    private lateinit var genre: Genre

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var mainNavigator: MainNavigator

    private lateinit var listWithToolbarTitle: ListWithToolbarTitle

    private lateinit var discoverViewModel: DiscoverViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        arguments?.let {
            genre =
                it.getParcelable(ARG_GENRE_ID) ?: throw RuntimeException("Genre Id cannot be null")
        }
        discoverViewModel =
            ViewModelProvider(this, myViewModelFactory).get(DiscoverViewModel::class.java)
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
        discoverViewModel.onStart(this.genre)
        registerObservers()
    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        discoverViewModel.onSavedInstanceState()
    }

    //User Interactions-----------------------------------------------------------------------------
    override fun onMovieItemClicked(movieID: Int) {
        mainNavigator.toDetailFragment(movieID)
    }

    override fun loadMoreItems() {
        discoverViewModel.loadMore()
    }

    override fun onRetryClicked() {
        discoverViewModel.onRetryClicked()
    }

    override fun onPaginationErrorClicked() {
        discoverViewModel.loadMore()
    }

    override fun onMenuIconClicked() {
        mainNavigator.toFilterFragment()
    }

    override fun onBackArrowClick() {
        mainNavigator.popFragment()
    }

    override fun onLoginRequiredBtnClicked() {

    }
    //----------------------------------------------------------------------------------------------


    private val featuredListScreenStateObserver =
        Observer<ListWithToolbarTitleState> { listWithToolbarState ->
            listWithToolbarTitle.handleScreenState(listWithToolbarState)
        }


    private fun registerObservers() {
        discoverViewModel.state.observeForever(featuredListScreenStateObserver)
        listWithToolbarTitle.registerListener(this)
    }

    private fun unregisterObservers() {
        discoverViewModel.state.removeObserver(featuredListScreenStateObserver)
        listWithToolbarTitle.unregisterListener(this)
    }

}