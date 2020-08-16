package com.hadysalhab.movid.screen.main.movielist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hadysalhab.movid.R

private const val ARG_GROUP_KEY = "arg_group_key"

class MovieListFragment : Fragment() {
    lateinit var groupType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupType = it.getString(ARG_GROUP_KEY)
                ?: throw RuntimeException("Cannot Start MovieListFragment without group type key")
        }
        Log.d("MovieListFragment", "onCreate: $groupType ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_movie_list, container, false)
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
}