package com.hadysalhab.movid.screen.main.moviedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.*
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.rating.Rating
import com.synnapps.carouselview.CarouselView

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
    private val castsFL: FrameLayout
    private val similarFL: FrameLayout
    private val recommendedFL: FrameLayout
    private lateinit var movieDetail: MovieDetail
    private val reviewCV: CardView
    private val movieReviewReviewTV: TextView
    private val movieReviewAuthorTV: TextView
    private val ratingFL: FrameLayout
    private val rating: Rating
    private val progressBar: ProgressBar
    private val detailSV: ScrollView

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_movie_detail, parent, false))
        progressBar = findViewById(R.id.detail_progress)
        detailSV = findViewById(R.id.movie_detail)
        carouselView = findViewById(R.id.carouselView)
        posterImageView = findViewById(R.id.iv_poster)
        movieOverviewTextView = findViewById(R.id.movie_overview)
        movieOverviewLL = findViewById(R.id.ll_movie_overview)
        movieTitleTextView = findViewById(R.id.tv_movie_title)
        movieTagLineTextView = findViewById(R.id.tv_movie_tagLine)
        similarFL = findViewById(R.id.fl_similar)
        factsLL = findViewById(R.id.ll_facts)
        recommendedFL = findViewById(R.id.fl_recommended)
        castsFL = findViewById(R.id.fl_casts)
        reviewCV = findViewById(R.id.fact_review)
        movieReviewAuthorTV = findViewById(R.id.movie_review_author)
        movieReviewReviewTV = findViewById(R.id.movie_review_review)
        ratingFL = findViewById(R.id.rating_wrapper)
        rating = viewFactory.getRatingView(ratingFL)
        ratingFL.addView(rating.getRootView())
    }

    override fun displayMovieDetail(movieDetail: MovieDetail) {
        // avoid re-rendering the view if it is already rendered
        if (!this::movieDetail.isInitialized) {
            this.movieDetail = movieDetail
            displayCarouselImages(movieDetail.images.backdrops)
            displayPosterImage(movieDetail.details.posterPath)
            displayOverview(movieDetail.details.overview)
            displayTitle(movieDetail.details.title)
            displayTagLine(movieDetail.details.tagLine ?: "")
            displayFacts(movieDetail.details)
            displayCasts(movieDetail.credits.cast)
            displaySimilarMovies(movieDetail.similar.movies)
            displayRecommendedMovies(movieDetail.recommendations.movies)
            displayReviews(movieDetail.reviews)
            displayRating(movieDetail.details.voteAvg, movieDetail.details.voteCount)
            progressBar.visibility = View.GONE
            detailSV.visibility = View.VISIBLE
        }
    }

    override fun displayLoadingScreen() {
        progressBar.visibility = View.VISIBLE
        detailSV.visibility = View.GONE
    }

    private fun displayRating(avg: Double, count: Int) {
        rating.displayRating(avg, count)
    }

    private fun displayReviews(reviews: Reviews) {
        if (reviews.review.size > 1) {
            val review = reviews.review[0]
            movieReviewReviewTV.text = review.content
            movieReviewAuthorTV.text = review.author
        } else {
            reviewCV.visibility = View.GONE
        }
    }

    private fun displayCasts(casts: List<Cast>) {
        if (casts.isNotEmpty()) {
            val castGroup = CastGroup(GroupType.CAST, casts)
            val castsViews = viewFactory.getCastsView(castsFL)
            castsViews.displayCardGroup(castGroup)
            castsFL.addView(castsViews.getRootView())
        } else {
            castsFL.visibility = View.GONE
        }
    }

    private fun displayFacts(movieInfo: MovieInfo) {
        movieInfo.revenue.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(getContext().getDrawable(R.drawable.ic_money)!!, it.toString())
            factsLL.addView(factView.getRootView())
        }
        movieInfo.homepage?.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(getContext().getDrawable(R.drawable.ic_web)!!, it)
            factsLL.addView(factView.getRootView())
        }
        movieInfo.popularity.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(
                getContext().getDrawable(R.drawable.ic_popularity)!!,
                it.toString()
            )
            factsLL.addView(factView.getRootView())
        }
        movieInfo.genres.let {
            val factView = viewFactory.getFactView(factsLL)
            val genres = it.joinToString { genre -> genre.name }
            factView.displayFact(getContext().getDrawable(R.drawable.ic_genre)!!, genres)
            factsLL.addView(factView.getRootView())
        }
        movieInfo.status.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(getContext().getDrawable(R.drawable.ic_status)!!, it)
            factsLL.addView(factView.getRootView())
        }
        movieInfo.originalLanguage.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(getContext().getDrawable(R.drawable.ic_language)!!, it)
            factsLL.addView(factView.getRootView())
        }
        movieInfo.runtime.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(getContext().getDrawable(R.drawable.ic_video)!!, "$it ")
            factsLL.addView(factView.getRootView())
        }
        movieInfo.releaseDate?.let {
            val factView = viewFactory.getFactView(factsLL)
            factView.displayFact(getContext().getDrawable(R.drawable.ic_date)!!, it)
            factsLL.addView(factView.getRootView())
        }

    }

    private fun displaySimilarMovies(movies: List<Movie>) {
        if (movies.isNotEmpty()) {
            val movieGroup = MovieGroup(GroupType.SIMILAR_MOVIES, movies)
            val movieGroupView = viewFactory.getMoviesView(similarFL)
            movieGroupView.displayCardGroup(movieGroup)
            similarFL.addView(movieGroupView.getRootView())
        } else {
            similarFL.visibility = View.GONE
        }
    }

    private fun displayRecommendedMovies(movies: List<Movie>) {
        if (movies.isNotEmpty()) {
            val movieGroup = MovieGroup(GroupType.RECOMMENDED_MOVIES, movies)
            val movieGroupView = viewFactory.getMoviesView(recommendedFL)
            movieGroupView.displayCardGroup(movieGroup)
            recommendedFL.addView(movieGroupView.getRootView())
        } else {
            recommendedFL.visibility = View.GONE
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
        carouselView.setImageListener { position, imageView ->
            Glide.with(getContext())
                .load(backdrops[position].filePath)
                .into(imageView)
        }
        carouselView.pageCount = backdrops.size
    }

    private fun displayPosterImage(posterPath: String?) {
        posterPath?.let {
            Glide.with(getContext())
                .load(it)
                .into(posterImageView)
        }

    }
}