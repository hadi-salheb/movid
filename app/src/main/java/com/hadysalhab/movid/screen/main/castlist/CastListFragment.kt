package com.hadysalhab.movid.screen.main.castlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

private const val MOVIE_ID = "MOVIE_ID"
private const val MOVIE_NAME = "MOVIE_NAME"

class CastListFragment : BaseFragment(), CastListView.Listener {
    @Inject
    lateinit var viewFactory: ViewFactory

    private lateinit var castListView: CastListView

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var castListViewModel: CastListViewModel

    private var movieID: Int? = null
    lateinit var movieName: String

    companion object {
        @JvmStatic
        fun newInstance(movieID: Int, movieName: String) =
            CastListFragment().apply {
                arguments = Bundle().apply {
                    putInt(MOVIE_ID, movieID)
                    putString(MOVIE_NAME, movieName)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        arguments?.let {
            movieID = it.getInt(MOVIE_ID)
            if (movieID == null) {
                throw RuntimeException("Cannot Start CastListFragment without movie id")
            }
            movieName = it.getString(MOVIE_NAME)
                ?: throw java.lang.RuntimeException("Cannot Start CastListFragment without a movie name")
        }
        castListViewModel =
            ViewModelProvider(this, viewModelFactory).get(CastListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::castListView.isInitialized) {
            castListView = viewFactory.getCastListView(container)
        }
        return this.castListView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        registerObservers()
        castListViewModel.onStart(movieID!!, movieName)
    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    private val castListViewStateObserver = Observer<CastListViewState> {
        castListView.handleState(it)
    }

    private fun registerObservers() {
        castListView.registerListener(this)
        castListViewModel.state.observeForever(castListViewStateObserver)
    }

    private fun unregisterObservers() {
        castListView.unregisterListener(this)
        castListViewModel.state.removeObserver(castListViewStateObserver)
    }

    override fun onBackArrowClicked() {
        mainNavigator.popFragment()
    }

    override fun onErrorRetryClicked() {
        castListViewModel.onRetryClicked()
    }


}

