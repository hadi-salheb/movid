package com.hadysalhab.movid.screen.main.moviedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hadysalhab.movid.R

private const val MOVIE_ID = "MOVIE_ID"


class MovieDetail : Fragment() {
    private var movieID: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movieID = it.getLong(MOVIE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =inflater.inflate(R.layout.fragment_movie_detail, container, false)
        val textview = v.findViewById<TextView>(R.id.movie_id)
        textview.text = movieID.toString()
        return v
    }

    companion object {
        @JvmStatic
        fun newInstance(movieID: Long) =
            MovieDetail().apply {
                arguments = Bundle().apply {
                    putLong(MOVIE_ID, movieID)
                }
            }
    }
}