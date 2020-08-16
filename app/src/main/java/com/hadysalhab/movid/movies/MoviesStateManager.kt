package com.hadysalhab.movid.movies

import android.os.Handler

class MoviesStateManager {
    init {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                clearData()
                handler.postDelayed(this, 24*60*60*1000)
            }
        }, 24*60*60*1000)
    }

    val movies = mutableListOf<MovieDetail>()
    val moviesGroup: MutableList<MovieGroup> = mutableListOf()
    fun areMoviesAvailabe() = moviesGroup.isNotEmpty()
    private fun clearData() {
        moviesGroup = emptyList()
        movies.clear()
    }
}