package com.hadysalhab.movid.screen.main.recommendedsimilar

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
private const val ARG_MOVIE_ID = "arg_movie_id"
private const val ARG_MOVIE_NAME = "arg_movie_name"

class RecommendedSimilarFragment : BaseFragment(), ListWithToolbarTitle.Listener {

    companion object {
        @JvmStatic
        fun newInstance(groupType: GroupType, movieID: Int, movieName: String) =
            RecommendedSimilarFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_GROUP_KEY, groupType)
                    putString(ARG_MOVIE_NAME, movieName)
                    putInt(ARG_MOVIE_ID, movieID)
                }
            }
    }

    lateinit var movieName: String
    lateinit var groupType: GroupType
    var movieID = -10


    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var mainNavigator: MainNavigator

    private lateinit var listWithToolbarTitle: ListWithToolbarTitle

    private lateinit var recommendedSimilarViewModel: RecommendedSimilarViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        injector.inject(this)

        arguments?.let {
            groupType = it.getParcelable(ARG_GROUP_KEY)!!
            movieName = it.getString(ARG_MOVIE_NAME)!!
            movieID = it.getInt(ARG_MOVIE_ID)
        }
        recommendedSimilarViewModel =
            ViewModelProvider(this, myViewModelFactory).get(RecommendedSimilarViewModel::class.java)
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
        recommendedSimilarViewModel.onStart(groupType, movieName, movieID)
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
        recommendedSimilarViewModel.loadMore()
    }

    override fun onRetryClicked() {
        recommendedSimilarViewModel.onRetryClicked()
    }

    override fun onPaginationErrorClicked() {
        recommendedSimilarViewModel.loadMore()
    }

    override fun onMenuIconClicked() {

    }
    //----------------------------------------------------------------------------------------------


    private val featuredListScreenStateObserver =
        Observer<ListWithToolbarTitleState> { listWithToolbarState ->
            listWithToolbarTitle.handleScreenState(listWithToolbarState)
        }


    private fun registerObservers() {
        recommendedSimilarViewModel.state.observeForever(featuredListScreenStateObserver)
        listWithToolbarTitle.registerListener(this)
    }

    private fun unregisterObservers() {
        recommendedSimilarViewModel.state.removeObserver(featuredListScreenStateObserver)
        listWithToolbarTitle.unregisterListener(this)
    }


}