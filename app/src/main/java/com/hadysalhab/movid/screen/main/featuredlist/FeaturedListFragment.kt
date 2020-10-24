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
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitle
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleState
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

private const val ARG_GROUP_KEY = "arg_group_key"
private const val ARG_REGION = "arg_region"

class FeaturedListFragment : BaseFragment(), ListWithToolbarTitle.Listener {

    companion object {
        @JvmStatic
        fun newInstance(groupType: GroupType, region: String) =
            FeaturedListFragment().apply {
                arguments = Bundle().apply {
                    region.let {
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

    private lateinit var listWithToolbarTitle: ListWithToolbarTitle

    private lateinit var featuredListViewModel: FeaturedListViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        injector.inject(this)

        arguments?.let {
            groupType = it.getParcelable(ARG_GROUP_KEY)!!
            region = it.getString(ARG_REGION)!!
        }
        featuredListViewModel =
            ViewModelProvider(this, myViewModelFactory).get(FeaturedListViewModel::class.java)
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

    override fun onRetryClicked() {
        featuredListViewModel.onRetryClicked()
    }

    override fun onPaginationErrorClicked() {
        featuredListViewModel.loadMore()
    }

    override fun onMenuIconClicked() {

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
        featuredListViewModel.state.observe(this, featuredListScreenStateObserver)
        listWithToolbarTitle.registerListener(this)
    }

    private fun unregisterObservers() {
        featuredListViewModel.state.removeObserver(featuredListScreenStateObserver)
        listWithToolbarTitle.unregisterListener(this)
    }


}