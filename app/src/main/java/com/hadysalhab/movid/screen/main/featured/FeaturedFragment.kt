package com.hadysalhab.movid.screen.main.featured

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hadysalhab.movid.movies.MovieGroupType
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import javax.inject.Inject


class FeaturedFragment : BaseFragment(), FeaturedView.Listener {

    companion object {

    }
    
    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context
    private lateinit var view: FeaturedView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = viewFactory.getFeaturedView(container)
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



    override fun onMovieCardClicked(movieID: Int) {
        Toast.makeText(activityContext, "Movie with $movieID clicked", Toast.LENGTH_LONG).show()
    }

    override fun onSeeAllClicked(movieGroupType: MovieGroupType) {
        Toast.makeText(activityContext, "See All for $movieGroupType is clicked", Toast.LENGTH_LONG)
            .show()
    }
}