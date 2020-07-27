package com.hadysalhab.movid.screen.main.featured

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.movies.MovieGroup
import com.hadysalhab.movid.movies.MovieGroupType
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
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
        val movieGroups = listOf<MovieGroup>(
            MovieGroup(
                MovieGroupType.POPULAR,
                listOf<Movie>(
                    Movie(1, "superman", "..", 2.2, 2, "2/02/2013"),
                    Movie(2, "batman", "..", 4.2, 5, "2/03/2015"),
                    Movie(3, "captain america", "..", 5.0, 3, "2/02/2016"),
                    Movie(4, "spider-man", "..", 3.5, 1, "2/02/2018"),
                    Movie(5, "warrior", "..", 4.6, 15, "2/02/2020")
                )
            ),
            MovieGroup(
                MovieGroupType.TOP_RATED,
                listOf<Movie>(
                    Movie(4, "superman", "..", 2.2, 2, "2/02/2013"),
                    Movie(1, "batman", "..", 4.2, 5, "2/03/2015"),
                    Movie(2, "captain america", "..", 5.0, 3, "2/02/2016"),
                    Movie(3, "spider-man", "..", 3.5, 1, "2/02/2018"),
                    Movie(7, "warrior", "..", 4.6, 15, "2/02/2020")
                )
            )
        )
        view.displayMovieGroups(movieGroups)
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