package com.hadysalhab.movid.screen.main.movielist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import javax.inject.Inject

private const val ARG_GROUP_KEY = "arg_group_key"

class MovieListFragment : BaseFragment(), MovieListView.Listener {
    lateinit var groupType: String

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var deviceConfigManager: DeviceConfigManager

    private lateinit var view: MovieListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        arguments?.let {
            groupType = it.getString(ARG_GROUP_KEY)
                ?: throw RuntimeException("Cannot Start MovieListFragment without group type key")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::view.isInitialized) {
            view = viewFactory.getMovieListView(container)
        }
        // Inflate the layout for this fragment
        return view.getRootView()
    }

    override fun onStart() {
        super.onStart()
        view.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        view.unregisterListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(groupType: String) =
            MovieListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_GROUP_KEY, groupType)
                }
            }
    }

    override fun onMovieItemClicked(movieID: Int) {
        Log.d("MovieListFragment", "onMovieItemClicked: $movieID ")
    }

}