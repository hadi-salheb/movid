package com.hadysalhab.movid.screen.main.moviedetail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Backdrops
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.MovieInfo
import com.hadysalhab.movid.screen.common.ViewFactory
import com.synnapps.carouselview.CarouselView
import java.util.concurrent.TimeUnit

class MovieDetailViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    private val viewFactory: ViewFactory
) : MovieDetailView() {
    private val carouselView: CarouselView
    private val posterImageView: ImageView
    private val movieOverviewTextView: TextView
    private val movieTitleTextView: TextView
    private val movieOverviewLL: LinearLayout
    private val movieTagLineTextView: TextView
    private val factsLL: LinearLayout

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_movie_detail, parent, false))
        carouselView = findViewById(R.id.carouselView)
        posterImageView = findViewById(R.id.iv_poster)
        movieOverviewTextView = findViewById(R.id.movie_overview)
        movieOverviewLL = findViewById(R.id.ll_movie_overview)
        movieTitleTextView = findViewById(R.id.tv_movie_title)
        movieTagLineTextView = findViewById(R.id.tv_movie_tagLine)
        factsLL = findViewById(R.id.ll_facts)
    }

    override fun displayMovieDetail(movieDetail: MovieDetail) {
        displayCarouselImages(movieDetail.images.backdrops)
        displayPosterImage(movieDetail.details.posterPath)
        displayOverview(movieDetail.details.overview)
        displayTitle(movieDetail.details.title)
        displayTagLine(movieDetail.details.tagLine ?: "")
        displayFacts(movieDetail.details)
    }

    private fun displayFacts(movieInfo: MovieInfo) {
        movieInfo.revenue?.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(getContext().getDrawable(R.drawable.ic_money)!!, it.toString())
            factsLL.addView(factView.getRootView())
        }
        movieInfo.homepage?.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(getContext().getDrawable(R.drawable.ic_web)!!,it)
            factsLL.addView(factView.getRootView())
        }
        movieInfo.popularity.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(getContext().getDrawable(R.drawable.ic_popularity)!!,it.toString())
            factsLL.addView(factView.getRootView())
        }
        movieInfo.genres.let {
            val factView = viewFactory.getFactView(factsLL)
            val genres = it.joinToString { genre -> genre.name }
            factView.displayFact(getContext().getDrawable(R.drawable.ic_genre)!!,genres)
            factsLL.addView(factView.getRootView())
        }
        movieInfo.status.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(getContext().getDrawable(R.drawable.ic_status)!!,it)
            factsLL.addView(factView.getRootView())
        }
        movieInfo.originalLanguage.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(getContext().getDrawable(R.drawable.ic_language)!!,it)
            factsLL.addView(factView.getRootView())
        }
        movieInfo.runtime.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(getContext().getDrawable(R.drawable.ic_video)!!,"$it ")
            factsLL.addView(factView.getRootView())
        }
        movieInfo.releaseDate?.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(getContext().getDrawable(R.drawable.ic_date)!!,it)
            factsLL.addView(factView.getRootView())
        }

    }

    private fun displayOverview(overview: String?) {
        if (overview == null) {
            movieOverviewLL.visibility = View.GONE
        } else {
            movieOverviewTextView.text = overview
        }
    }

    private fun displayTitle(title: String) {
        movieTitleTextView.text = title
    }

    private fun displayTagLine(tagLine: String) {
        movieTagLineTextView.text = tagLine
    }

    private fun displayCarouselImages(backdrops: List<Backdrops>) {
        Log.d("TAG", "displayCarouselImages: $backdrops")

        carouselView.setImageListener { position, imageView ->
            Glide.with(getContext())
                .load(backdrops[position].filePath)
                .into(imageView)
        }
        carouselView.pageCount = backdrops.size
    }

    private fun displayPosterImage(posterPath: String?) {
        Log.d("TAG", "displayPosterImage: $posterPath")
        posterPath?.let {
            Glide.with(getContext())
                .load(it)
                .into(posterImageView)
        }

    }
}