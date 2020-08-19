package com.hadysalhab.movid.screen.main.featured

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject


class FeaturedFragment : BaseFragment(), FeaturedView.Listener {
    companion object {
        @JvmStatic
        fun newInstance() = FeaturedFragment()
    }

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var mainNavigator: MainNavigator
    @Inject
    lateinit var myViewModelFactory: ViewModelFactory
    private lateinit var view: FeaturedView
    private lateinit var featuredViewModel: FeaturedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        featuredViewModel =
            ViewModelProvider(this, myViewModelFactory).get(FeaturedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::view.isInitialized) {
            view = viewFactory.getFeaturedView(container)
        }
        return view.getRootView()
    }

    override fun onStart() {
        super.onStart()
        view.registerListener(this)
        featuredViewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            render(viewState)
        })
    }

    override fun onStop() {
        super.onStop()
        view.unregisterListener(this)
    }


    override fun onMovieCardClicked(movieID: Int) {
        mainNavigator.toDetailFragment(movieID)
    }

    override fun onSeeAllClicked(groupType: GroupType) {
        Toast.makeText(activityContext, "See All for $groupType is clicked", Toast.LENGTH_LONG)
            .show()
        mainNavigator.toMovieListFragment(groupType.value)
    }


    private fun displayMovies(movieGroups: List<MoviesResponse>) {
        view.displayMovieGroups(movieGroups.sortedBy { item -> item.tag.ordinal }
            .filter { !it.movies.isNullOrEmpty() })
    }

    private fun render(viewState: FeaturedViewState) {
        when (viewState) {
            Loading -> view.displayLoadingScreen()
            is Error -> Toast.makeText(
                activityContext,
                "error: ${viewState.errorMessage}",
                Toast.LENGTH_LONG
            ).show()
            is FeaturedLoaded -> displayMovies(viewState.moviesResponseList)
        }
    }
}