package com.hadysalhab.movid.screen.main.castlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import javax.inject.Inject

private const val MOVIE_ID = "MOVIE_ID"
private const val MOVIE_NAME = "MOVIE_NAME"

class CastListFragment : BaseFragment(), CastListView.Listener {
    @Inject
    lateinit var viewFactory: ViewFactory

    lateinit var castListView: CastListView

    @Inject
    lateinit var mainNavigator: MainNavigator

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
    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }


    private fun registerObservers() {
        castListView.registerListener(this)
    }

    private fun unregisterObservers() {
        castListView.unregisterListener(this)
    }

    override fun onBackArrowClicked() {
        mainNavigator.popFragment()
    }


}

