package com.hadysalhab.movid.screen.main.castlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.people.PeopleType
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

private const val MOVIE_ID = "MOVIE_ID"
private const val MOVIE_NAME = "MOVIE_NAME"
private const val PEOPLE_TYPE = "PEOPLE_TYPE"

class PeopleListFragment : BaseFragment(), PeopleListView.Listener {
    @Inject
    lateinit var viewFactory: ViewFactory

    private lateinit var peopleListView: PeopleListView

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var peopleListViewModel: PeopleListViewModel

    private var movieID: Int? = null
    lateinit var peopleType: PeopleType
    lateinit var movieName: String

    companion object {
        @JvmStatic
        fun newInstance(movieID: Int, movieName: String, peopleType: PeopleType) =
            PeopleListFragment().apply {
                arguments = Bundle().apply {
                    putInt(MOVIE_ID, movieID)
                    putString(MOVIE_NAME, movieName)
                    putParcelable(PEOPLE_TYPE, peopleType)
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
            peopleType = it.getParcelable<PeopleType>(PEOPLE_TYPE)
                ?: throw  java.lang.RuntimeException("People type must be defined in PeopleListFragment")
        }
        peopleListViewModel =
            ViewModelProvider(this, viewModelFactory).get(PeopleListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::peopleListView.isInitialized) {
            peopleListView = viewFactory.getCastListView(container)
        }
        return this.peopleListView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        registerObservers()
        peopleListViewModel.onStart(movieID!!, movieName, peopleType)
    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    private val castListViewStateObserver = Observer<PeopleListViewState> {
        peopleListView.handleState(it)
    }

    private fun registerObservers() {
        peopleListView.registerListener(this)
        peopleListViewModel.state.observeForever(castListViewStateObserver)
    }

    private fun unregisterObservers() {
        peopleListView.unregisterListener(this)
        peopleListViewModel.state.removeObserver(castListViewStateObserver)
    }

    override fun onBackArrowClicked() {
        mainNavigator.popFragment()
    }

    override fun onErrorRetryClicked() {
        peopleListViewModel.onRetryClicked()
    }


}

