package com.hadysalhab.movid.screen.main.moviedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.constants.IMAGES_BASE_URL
import com.hadysalhab.movid.common.constants.POSTER_SIZE_185
import com.hadysalhab.movid.common.utils.convertDpToPixel
import com.hadysalhab.movid.movies.*
import com.hadysalhab.movid.movies.Collection
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.cardgroup.CastsView
import com.hadysalhab.movid.screen.common.cardgroup.DataGroup
import com.hadysalhab.movid.screen.common.cardgroup.MoviesView
import com.synnapps.carouselview.CarouselView

class MovieDetailViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    private val viewFactory: ViewFactory
) : MovieDetailView(), MoviesView.Listener, CastsView.Listener {
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
    private val collectionFL: FrameLayout
    private var movieDetail: MovieDetail? = null
    private val reviewCV: CardView
    private val movieReviewReviewTV: TextView
    private val movieReviewAuthorTV: TextView
    private val ratingFL: FrameLayout
    private val progressBar: ProgressBar
    private val detailSV: ScrollView
    private val reviewsBtn: Button
    private val trailerBtn: Button
    private val btnWrapperLL: LinearLayout
    private val favoriteBtn: Button
    private val watchlistBtn: Button

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
        collectionFL = findViewById(R.id.fl_collection)
        castsFL = findViewById(R.id.fl_casts)
        reviewCV = findViewById(R.id.fact_review)
        movieReviewAuthorTV = findViewById(R.id.movie_review_author)
        movieReviewReviewTV = findViewById(R.id.movie_review_review)
        ratingFL = findViewById(R.id.rating_wrapper)
        reviewsBtn = findViewById(R.id.btn_reviews)
        trailerBtn = findViewById(R.id.button_trailer)
        btnWrapperLL = findViewById(R.id.button_wrapper)
        favoriteBtn = findViewById(R.id.button_favorite)
        watchlistBtn = findViewById(R.id.button_watchlist)
        trailerBtn.setOnClickListener {
            listeners.forEach {
                it.onSeeTrailerClicked(this.movieDetail!!.videosResponse)
            }
        }
        favoriteBtn.setOnClickListener {
            listeners.forEach { listener ->
                this.movieDetail?.let {
                    listener.onFavBtnClick(it.details.id, it.accountStates.favorite)
                }
            }
        }
        watchlistBtn.setOnClickListener {
            listeners.forEach { listener ->
                this.movieDetail?.let {
                    listener.onWatchlistBtnClick(it.details.id, it.accountStates.watchlist)
                }
            }
        }
    }

    override fun displayMovieDetail(movieDetail: MovieDetail) {
        // avoid re-rendering the view if it is already rendered
        if (this.movieDetail == null) {
            this.movieDetail = movieDetail
            displayCarouselImages(movieDetail.images.backdrops)
            displayPosterImage(movieDetail.details.posterPath)
            displayOverview(movieDetail.details.overview)
            displayTitle(movieDetail.details.title)
            displayTagLine(movieDetail.details.tagLine)
            displayFacts(movieDetail.details)
            displayCasts(movieDetail.credits.cast)
            displaySimilarMovies(movieDetail.similar)
            displayRecommendedMovies(movieDetail.recommendations)
            displayReviews(movieDetail.reviewResponse, movieDetail.details.id)
            displayRating(movieDetail.details.voteAvg, movieDetail.details.voteCount)
            displayAccountState(movieDetail.accountStates)
            displayCollection(movieDetail.collection)
        } else if (this.movieDetail != movieDetail) {
            if (this.movieDetail!!.accountStates != movieDetail.accountStates) {
                displayAccountState(movieDetail.accountStates)
            }
            this.movieDetail = movieDetail
        }
        progressBar.visibility = View.GONE
        detailSV.visibility = View.VISIBLE
    }

    private fun displayAccountState(accountStates: AccountStates) {
        if (accountStates.favorite) {
            favoriteBtn.text = "Remove From Favorites"
            favoriteBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white))
            favoriteBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_600))
        } else {
            favoriteBtn.text = "Add To Favorites"
            favoriteBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_900))
            favoriteBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white))
        }
        if (accountStates.watchlist) {
            watchlistBtn.text = "Remove From Watchlist"
            watchlistBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white))
            watchlistBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_600))
        } else {
            watchlistBtn.text = "Add To Watchlist"
            watchlistBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_900))
            watchlistBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white))
        }
    }

    override fun displayLoadingScreen() {
        progressBar.visibility = View.VISIBLE
        detailSV.visibility = View.GONE
    }

    override fun hideTrailerButton() {
        trailerBtn.visibility = View.GONE
        btnWrapperLL.setPadding(0, convertDpToPixel(8, getContext()), 0, 0)
    }

    override fun displayAccountStateLoading() {
        // TODO: CHANGE IT LATER
        progressBar.visibility = View.VISIBLE
    }

    private fun displayRating(avg: Double, count: Int) {
        val rating = viewFactory.getRatingView(ratingFL)
        rating.displayRating(avg, count)
        ratingFL.removeAllViews()
        ratingFL.addView(rating.getRootView())
    }

    private fun displayReviews(reviewResponse: ReviewResponse, movieID: Int) {
        if (reviewResponse.reviews.size > 1) {
            val review = reviewResponse.reviews[0]
            movieReviewReviewTV.text = review.content
            movieReviewAuthorTV.text = review.author
            reviewsBtn
            reviewsBtn.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    listeners.forEach {
                        it.onSeeReviewsClicked(movieID)
                    }
                }
            }
        } else {
            movieReviewReviewTV.text = "No Reviews Available"
            reviewsBtn.visibility = View.GONE
        }
    }

    private fun displayCasts(casts: List<Cast>) {
        if (casts.isNotEmpty()) {
            val castsViews = viewFactory.getCastsView(castsFL)
            castsViews.registerListener(this)
            castsViews.renderData(DataGroup(GroupType.CAST, casts), 5)
            castsFL.removeAllViews()
            castsFL.addView(castsViews.getRootView())
        } else {
            castsFL.visibility = View.GONE
        }
    }

    private fun displayFacts(movieInfo: MovieInfo) {
        factsLL.removeAllViews()
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

    private fun displayCollection(collection: Collection?) {
        if (collection != null) {
            val groupType = GroupType.COLLECTION
            groupType.value = "From ${collection.name}"

            val movieGroupView = viewFactory.getMoviesView(collectionFL)
            movieGroupView.registerListener(this)
            movieGroupView.renderData(
                DataGroup(
                    groupType,
                    collection.movies.filter { movie -> movie.id != this.movieDetail!!.details.id }
                ), null
            )
            collectionFL.removeAllViews()
            collectionFL.addView(movieGroupView.getRootView())
        } else {
            collectionFL.visibility = View.GONE
        }
    }

    private fun displaySimilarMovies(moviesResponse: MoviesResponse) {
        if (!moviesResponse.movies.isNullOrEmpty()) {
            val movieGroupView = viewFactory.getMoviesView(similarFL)
            movieGroupView.registerListener(this)
            movieGroupView.renderData(
                DataGroup(
                    moviesResponse.tag,
                    moviesResponse.movies
                ), 5
            )
            similarFL.removeAllViews()
            similarFL.addView(movieGroupView.getRootView())
        } else {
            similarFL.visibility = View.GONE
        }
    }

    private fun displayRecommendedMovies(moviesResponse: MoviesResponse) {
        if (!moviesResponse.movies.isNullOrEmpty()) {
            val movieGroupView = viewFactory.getMoviesView(recommendedFL)
            movieGroupView.renderData(
                DataGroup(
                    moviesResponse.tag,
                    moviesResponse.movies
                ), 5
            )
            movieGroupView.registerListener(this)
            recommendedFL.removeAllViews()
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

    private fun displayTagLine(tagLine: String?) {
        if (tagLine == null) {
            movieTagLineTextView.text = tagLine
        } else {
            movieTagLineTextView.visibility = View.GONE
        }
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
                .load(IMAGES_BASE_URL + POSTER_SIZE_185 + it)
                .into(posterImageView)
        }

    }

    override fun onMovieCardClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieClicked(movieID)
        }
    }

    override fun onCastCardClicked(castID: Int) {
        listeners.forEach {
            it.onCastClicked(castID)
        }
    }

    override fun onSeeAllClicked(groupType: GroupType) {
        listeners.forEach {
            it.onSeeAllClicked(groupType)
        }
    }

}