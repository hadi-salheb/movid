package com.hadysalhab.movid.screen.main.featuredlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

private const val ARG_GROUP_KEY = "arg_group_key"
private const val ARG_MOVIE_ID = "arg_movie_id"
private const val ARG_REGION = "arg_region"

class FeaturedListFragment : BaseFragment(), FeaturedListScreen.Listener {

    companion object {
        @JvmStatic
        fun newInstance(groupType: GroupType, movieID: Int?, region: String?) =
            FeaturedListFragment().apply {
                arguments = Bundle().apply {
                    movieID?.let {
                        putInt(ARG_MOVIE_ID, it)
                    }
                    region?.let {
                        putString(ARG_REGION, it)
                    }
                    putParcelable(ARG_GROUP_KEY, groupType)
                }
            }
    }

    lateinit var region: String
    lateinit var groupType: GroupType

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var mainNavigator: MainNavigator

    private lateinit var featuredListScreen: FeaturedListScreen

    private lateinit var featuredListViewModel: FeaturedListViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        injector.inject(this)

        arguments?.let {
            groupType = it.getParcelable(ARG_GROUP_KEY)
                ?: throw RuntimeException("Cannot Start FeaturedListFragment without GroupType key")
            region = it.getString(ARG_REGION)
                ?: throw java.lang.RuntimeException("Cannot Start FeaturedListFragment without region")
        }
        featuredListViewModel =
            ViewModelProvider(this, myViewModelFactory).get(FeaturedListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::featuredListScreen.isInitialized) {
            featuredListScreen = viewFactory.getFeaturedListScreen(container)
        }
        // Inflate the layout for this fragment
        return featuredListScreen.getRootView()
    }

    override fun onStart() {
        super.onStart()
        featuredListViewModel.onStart(groupType, region)
        registerObservers()
    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    //User Interactions-----------------------------------------------------------------------------
    override fun onMovieItemClicked(movieID: Int) {
        mainNavigator.toDetailFragment(movieID)
    }

    override fun loadMoreItems() {
        featuredListViewModel.loadMore()
    }
    //----------------------------------------------------------------------------------------------


    private val featuredListScreenStateObserver =
        Observer<FeaturedListScreenState> { featuredListScreenState ->
            featuredListScreen.handleScreenState(featuredListScreenState)
        }


    private fun registerObservers() {
        featuredListViewModel.state.observe(this, featuredListScreenStateObserver)
        featuredListScreen.registerListener(this)
    }

    private fun unregisterObservers() {
        featuredListViewModel.state.removeObserver(featuredListScreenStateObserver)
        featuredListScreen.unregisterListener(this)
    }


}