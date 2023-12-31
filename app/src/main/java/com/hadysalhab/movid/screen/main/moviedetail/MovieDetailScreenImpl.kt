package com.hadysalhab.movid.screen.main.moviedetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.constants.IMAGES_BASE_URL
import com.hadysalhab.movid.common.constants.POSTER_SIZE_500
import com.hadysalhab.movid.common.utils.getYoutubeTrailerFromResponse
import com.hadysalhab.movid.movies.*
import com.hadysalhab.movid.movies.Collection
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.cardgroup.DataGroup
import com.hadysalhab.movid.screen.common.cardgroup.MoviesView
import com.hadysalhab.movid.screen.common.cardgroup.PeopleGroupView
import com.hadysalhab.movid.screen.common.errorscreen.ErrorScreen
import com.hadysalhab.movid.screen.common.loading.LoadingView
import com.hadysalhab.movid.screen.common.people.People
import com.hadysalhab.movid.screen.common.people.PeopleType
import com.synnapps.carouselview.CarouselView
import java.text.SimpleDateFormat
import java.util.*

class MovieDetailScreenImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    private val viewFactory: ViewFactory
) : MovieDetailScreen(), MoviesView.Listener, PeopleGroupView.Listener,
    SwipeRefreshLayout.OnRefreshListener, ErrorScreen.Listener {

    //loading
    private val loadingViewFrame: FrameLayout
    private val loadingView: LoadingView


    //error
    private val errorScreenFrame: FrameLayout
    private val errorScreen: ErrorScreen

    //data
    private val pullToRefresh: SwipeRefreshLayout
    private lateinit var movieDetail: MovieDetail
    private val detailSV: ScrollView

    private val carouselView: CarouselView
    private val posterImageView: ImageView
    private val movieTitleTextView: TextView
    private val movieTagLineTextView: TextView
    private val ratingFL: FrameLayout

    private val trailerBtn: Button
    private val btnWrapperLL: LinearLayout
    private val favoriteBtn: Button
    private val watchlistBtn: Button
    private val rateBtn: Button

    private val movieOverviewLL: LinearLayout
    private val movieOverviewTextView: TextView

    private val factsLL: LinearLayout

    private val castsFL: FrameLayout
    private val crewsFL: FrameLayout

    private val reviewCV: CardView
    private val movieReviewReviewTV: TextView
    private val movieReviewAuthorTV: TextView
    private val reviewsBtn: Button

    private val collectionFL: FrameLayout
    private val recommendedFL: FrameLayout
    private val similarFL: FrameLayout
    private val backArrow: ImageView

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_movie_detail, parent, false))

        //error
        errorScreenFrame = findViewById(R.id.error_screen_placeholder)
        errorScreen = viewFactory.getErrorScreen(errorScreenFrame)
        errorScreen.registerListener(this)
        errorScreenFrame.addView(errorScreen.getRootView())

        //loading
        loadingViewFrame = findViewById(R.id.loading_screen_placeholder)
        loadingView = viewFactory.getLoadingView(loadingViewFrame)
        loadingViewFrame.addView(loadingView.getRootView())

        //data
        pullToRefresh = findViewById(R.id.pull_to_refresh)
        pullToRefresh.setOnRefreshListener(this)
        pullToRefresh.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.teal_600))

        backArrow = findViewById(R.id.back_arrow)
        backArrow.setOnClickListener {
            listeners.forEach {
                it.onBackArrowClicked()
            }
        }

        detailSV = findViewById(R.id.movie_detail)
        carouselView = findViewById(R.id.carouselView)
        posterImageView = findViewById(R.id.iv_poster)
        movieOverviewTextView = findViewById(R.id.movie_overview)
        movieOverviewLL = findViewById(R.id.ll_movie_overview)
        movieTitleTextView = findViewById(R.id.tv_movie_title)
        movieTagLineTextView = findViewById(R.id.tv_movie_tagLine)
        similarFL = findViewById(R.id.fl_similar)
        factsLL = findViewById(R.id.ll_facts_placeholder)
        recommendedFL = findViewById(R.id.fl_recommended)
        collectionFL = findViewById(R.id.fl_collection)
        castsFL = findViewById(R.id.fl_casts)
        crewsFL = findViewById(R.id.fl_crews)
        reviewCV = findViewById(R.id.fact_review)
        movieReviewAuthorTV = findViewById(R.id.movie_review_author)
        movieReviewReviewTV = findViewById(R.id.movie_review_review)
        ratingFL = findViewById(R.id.rating_wrapper)
        reviewsBtn = findViewById(R.id.btn_reviews)
        trailerBtn = findViewById(R.id.button_trailer)
        btnWrapperLL = findViewById(R.id.button_wrapper)
        favoriteBtn = findViewById(R.id.button_favorite)
        watchlistBtn = findViewById(R.id.button_watchlist)
        rateBtn = findViewById(R.id.button_rate)

        trailerBtn.setOnClickListener {
            listeners.forEach {
                it.onSeeTrailerClicked(this.movieDetail.videosResponse)
            }
        }
        favoriteBtn.setOnClickListener {
            listeners.forEach { listener ->
                listener.onFavBtnClick()
            }
        }
        watchlistBtn.setOnClickListener {
            listeners.forEach { listener ->
                listener.onWatchlistBtnClick()
            }
        }
        rateBtn.setOnClickListener {
            listeners.forEach { listener ->
                listener.onRateBtnClick()

            }
        }
    }


    override fun showLoadingIndicator() {
        loadingViewFrame.visibility = View.VISIBLE
    }

    override fun hideLoadingIndicator() {
        loadingViewFrame.visibility = View.GONE
    }

    override fun showErrorScreen(error: String) {
        errorScreenFrame.visibility = View.VISIBLE
        errorScreen.displayErrorMessage(error)
    }

    override fun hideErrorScreen() {
        errorScreenFrame.visibility = View.GONE
    }

    override fun disablePullRefresh() {
        pullToRefresh.isEnabled = false
    }

    override fun enablePullRefresh() {
        pullToRefresh.isEnabled = true

    }

    override fun showTrailerIndicator() {
        trailerBtn.visibility = View.VISIBLE
        btnWrapperLL.setPadding(0, 0, 0, 0)
    }

    override fun hideTrailerIndicator() {
        trailerBtn.visibility = View.GONE
//        btnWrapperLL.setPadding(0, convertDpToPixel(8, getContext()), 0, 0)
    }

    override fun handleScreenState(movieDetailScreenState: MovieDetailScreenState) {
        if (movieDetailScreenState.isLoading) {
            showLoadingIndicator()
        } else {
            hideLoadingIndicator()
        }
        if (movieDetailScreenState.isRefreshing) {
            showRefreshIndicator()
        } else {
            hideRefreshIndicator()
        }
        if (movieDetailScreenState.data != null) {
            displayMovieDetail(movieDetailScreenState.data)
            if (getYoutubeTrailerFromResponse(movieDetailScreenState.data.videosResponse) != null) {
                showTrailerIndicator()
            } else {
                hideTrailerIndicator()
            }
        } else {
            hideMovieDetail()
        }
        if (movieDetailScreenState.error != null) {
            showErrorScreen(movieDetailScreenState.error)
        } else {
            hideErrorScreen()
        }
        if (movieDetailScreenState.isLoading || movieDetailScreenState.error != null) {
            disablePullRefresh()
        } else {
            enablePullRefresh()
        }
    }


    override fun showRefreshIndicator() {
        pullToRefresh.isRefreshing = true
    }

    override fun hideRefreshIndicator() {
        pullToRefresh.isRefreshing = false
    }

    override fun hideMovieDetail() {
        pullToRefresh.visibility = View.GONE
    }


    override fun displayMovieDetail(movieDetail: MovieDetail) {
        pullToRefresh.visibility = View.VISIBLE
        //first render
        if (!this::movieDetail.isInitialized) {
            this.movieDetail = movieDetail
            displayCarouselImages(movieDetail.images.backdrops)
            displayPosterImage(movieDetail.details.posterPath)
            displayTitle(movieDetail.details.title)
            displayTagLine(movieDetail.details.tagLine)
            displayRating(movieDetail.details.voteAvg, movieDetail.details.voteCount)
            if (movieDetail.accountStates != null) {
                displayAccountState(movieDetail.accountStates)
            }
            displayOverview(movieDetail.details.overview)
            displayFacts(movieDetail.details)
            displayCasts(movieDetail.credits.cast)
            displayCrews(movieDetail.credits.crew)
            displayReviews(
                movieDetail.reviewResponse,
                movieDetail.details.id,
                movieDetail.details.title
            )
            displayCollection(movieDetail.collection)
            displayRecommendedMovies(movieDetail.recommendations)
            displaySimilarMovies(movieDetail.similar)
        } else {
            if (this.movieDetail.images.backdrops != movieDetail.images.backdrops) {
                displayCarouselImages(movieDetail.images.backdrops)
            }
            if (this.movieDetail.details.posterPath != movieDetail.details.posterPath) {
                displayPosterImage(movieDetail.details.posterPath)
            }
            if (this.movieDetail.details.title != movieDetail.details.title) {
                displayTitle(movieDetail.details.title)
            }
            if (this.movieDetail.details.tagLine != movieDetail.details.tagLine) {
                displayTagLine(movieDetail.details.tagLine)
            }
            if (this.movieDetail.details.voteAvg != movieDetail.details.voteAvg || this.movieDetail.details.voteCount != movieDetail.details.voteCount) {
                displayRating(movieDetail.details.voteAvg, movieDetail.details.voteCount)
            }
            if (movieDetail.accountStates != null) {
                if (this.movieDetail.accountStates != movieDetail.accountStates) {
                    displayAccountState(movieDetail.accountStates)
                }
            }
            if (this.movieDetail.details.overview != movieDetail.details.overview) {
                displayOverview(movieDetail.details.overview)
            }
            if (this.movieDetail.details != movieDetail.details) {
                displayFacts(movieDetail.details)
            }
            if (this.movieDetail.reviewResponse != movieDetail.reviewResponse) {
                displayReviews(
                    movieDetail.reviewResponse,
                    movieDetail.details.id,
                    movieDetail.details.title
                )
            }
            if (this.movieDetail.collection != movieDetail.collection) {
                displayCollection(movieDetail.collection)
            }
            if (this.movieDetail.recommendations != movieDetail.recommendations) {
                displayRecommendedMovies(movieDetail.recommendations)
            }
            if (this.movieDetail.similar != movieDetail.similar) {
                displaySimilarMovies(movieDetail.similar)
            }
            this.movieDetail = movieDetail
        }
    }


    //Helper Functions------------------------------------------------------------------------------
    private fun displayCarouselImages(backdrops: List<Backdrops>) {
        carouselView.setImageListener { position, imageView ->
            Glide.with(getContext())
                .load(backdrops[position].filePath)
                .into(imageView)
        }
        carouselView.pageCount = backdrops.size
    }

    private fun displayPosterImage(posterPath: String?) {
        if (posterPath == null) {
            Glide.with(getContext())
                .load(R.drawable.image_not_found)
                .centerCrop()
                .into(posterImageView)
        } else {
            Glide.with(getContext())
                .load(IMAGES_BASE_URL + POSTER_SIZE_500 + posterPath)
                .into(posterImageView)
        }
    }

    private fun displayTitle(title: String) {
        movieTitleTextView.text = title
    }

    private fun displayTagLine(tagLine: String?) {
        if (tagLine == null || tagLine.isEmpty()) {
            movieTagLineTextView.visibility = View.GONE
        } else {
            movieTagLineTextView.text = tagLine
        }
    }

    private fun displayOverview(overview: String?) {
        if (overview == null || overview.isEmpty()) {
            movieOverviewLL.visibility = View.GONE
        } else {
            movieOverviewTextView.text = overview
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun displayFacts(movieInfo: MovieInfo) {
        factsLL.removeAllViews()
        movieInfo.budget.let {
            if (it != 0L) {
                val factView = viewFactory.getFactView(factsLL)
                factView.displayFact(
                    getContext().getDrawable(R.drawable.ic_money)!!,
                    "%,d".format(it) + " (Budget)"
                )
                factsLL.addView(factView.getRootView())
            }
        }
        movieInfo.revenue.let {
            if (it != 0L) {
                val factView = viewFactory.getFactView(factsLL)
                factView.displayFact(
                    getContext().getDrawable(R.drawable.ic_money)!!,
                    "%,d".format(it) + " (Revenue)"
                )
                factsLL.addView(factView.getRootView())
            }
        }

        movieInfo.genres.let {
            if (it.isNotEmpty()) {
                val factView = viewFactory.getFactView(factsLL)
                val genres = it.joinToString { genre -> genre.name }
                factView.displayFact(getContext().getDrawable(R.drawable.ic_genre)!!, genres)
                factsLL.addView(factView.getRootView())
            }
        }
        movieInfo.status.let {
            if (it.isNotEmpty()) {
                val factView = viewFactory.getFactView(factsLL)
                factView.displayFact(getContext().getDrawable(R.drawable.ic_status)!!, it)
                factsLL.addView(factView.getRootView())
            }
        }
        movieInfo.originalLanguage.let {
            if (it.isNotEmpty()) {
                val codeLocale = Locale(it)
                val englishLocale = Locale("en")
                val languageName = codeLocale.getDisplayLanguage(englishLocale)
                val factView = viewFactory.getFactView(factsLL)
                factView.displayFact(
                    getContext().getDrawable(R.drawable.ic_language)!!,
                    languageName
                )
                factsLL.addView(factView.getRootView())
            }
        }
        movieInfo.runtime.let {
            if (it != null && it != 0) {
                val factView = viewFactory.getFactView(factsLL)
                factView.displayFact(getContext().getDrawable(R.drawable.ic_video)!!, "$it minutes")
                factsLL.addView(factView.getRootView())
            }
        }
        movieInfo.releaseDate?.let { releaseDate ->
            if (releaseDate.isNotEmpty()) {
                val parser = SimpleDateFormat("yyyy-MM-dd")
                val formatter = SimpleDateFormat("MMM dd, yyyy.")
                val output = formatter.format(parser.parse(releaseDate)!!)
                val factView = viewFactory.getFactView(factsLL)
                factView.displayFact(getContext().getDrawable(R.drawable.ic_date)!!, output)
                factsLL.addView(factView.getRootView())
            }
        }
    }

    private fun displayAccountState(accountStates: AccountStates) {
        if (accountStates.favorite) {
            favoriteBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white_milky))
            favoriteBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_600))
        } else {
            favoriteBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_800))
            favoriteBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white))
        }
        if (accountStates.watchlist) {
            watchlistBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white_milky))
            watchlistBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_600))
        } else {
            watchlistBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_800))
            watchlistBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white))
        }
        if (accountStates.rated == null) {
            rateBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_800))
            rateBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white))
            rateBtn.text = getContext().getString(R.string.rate_movie)
        } else {
            rateBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white_milky))
            rateBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_600))
            rateBtn.text = getContext().getString(R.string.user_rating, accountStates.rated)
        }
    }

    private fun displayCasts(people: List<Cast>) {
        castsFL.removeAllViews()
        if (people.isNotEmpty()) {
            val peopleGroupView = viewFactory.getPeopleGroupView(castsFL)
            peopleGroupView.registerListener(this)
            peopleGroupView.renderData(DataGroup(GroupType.CAST, people.map {
                People(it.id, it.name, it.character, it.profilePath, PeopleType.CAST)
            }), 5)
            castsFL.addView(peopleGroupView.getRootView())
        } else {
            castsFL.visibility = View.GONE
        }
    }

    private fun displayCrews(crews: List<Crew>) {
        crewsFL.removeAllViews()
        if (crews.isNotEmpty()) {
            val peopleGroupView = viewFactory.getPeopleGroupView(crewsFL)
            peopleGroupView.registerListener(this)
            peopleGroupView.renderData(
                DataGroup(
                    GroupType.CREW,
                    crews.sortedBy { crew -> crew.profilePath == null }.map {
                        People(it.id, it.name, it.job, it.profilePath, PeopleType.CREW)
                    }), 5
            )
            crewsFL.addView(peopleGroupView.getRootView())
        } else {
            crewsFL.visibility = View.GONE
        }
    }

    private fun displayRating(avg: Double, count: Int) {
        ratingFL.removeAllViews()
        val rating = viewFactory.getRatingView(ratingFL)
        rating.displayRating(avg, count)
        ratingFL.addView(rating.getRootView())
    }

    private fun displayReviews(reviewResponse: ReviewResponse, movieID: Int, movieName: String) {
        if (reviewResponse.reviews.size > 1) {
            val review = reviewResponse.reviews[0]
            movieReviewReviewTV.text = review.content
            movieReviewAuthorTV.text = review.author
            reviewsBtn
            reviewsBtn.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    listeners.forEach {
                        it.onSeeReviewsClicked(movieID, movieName)
                    }
                }
            }
        } else {
            movieReviewReviewTV.text = getContext().getString(R.string.no_reviews)
            reviewsBtn.visibility = View.GONE
        }
    }

    private fun displayCollection(collection: Collection?) {
        collectionFL.removeAllViews()
        if (collection != null) {
            val groupType = GroupType.COLLECTION
            groupType.value = "From ${collection.name}"

            val movieGroupView = viewFactory.getMoviesView(collectionFL)
            movieGroupView.registerListener(this)
            movieGroupView.renderData(
                DataGroup(
                    groupType,
                    collection.movies.filter { movie -> movie.id != this.movieDetail.details.id }
                ), null
            )
            collectionFL.addView(movieGroupView.getRootView())
        } else {
            collectionFL.visibility = View.GONE
        }
    }

    private fun displaySimilarMovies(moviesResponse: MoviesResponse) {
        similarFL.removeAllViews()
        if (!moviesResponse.movies.isNullOrEmpty()) {
            val movieGroupView = viewFactory.getMoviesView(similarFL)
            movieGroupView.registerListener(this)
            movieGroupView.renderData(
                DataGroup(
                    moviesResponse.tag,
                    moviesResponse.movies
                ), 5
            )
            similarFL.addView(movieGroupView.getRootView())
        } else {
            similarFL.visibility = View.GONE
        }
    }

    private fun displayRecommendedMovies(moviesResponse: MoviesResponse) {
        recommendedFL.removeAllViews()
        if (!moviesResponse.movies.isNullOrEmpty()) {
            val movieGroupView = viewFactory.getMoviesView(recommendedFL)
            movieGroupView.renderData(
                DataGroup(
                    moviesResponse.tag,
                    moviesResponse.movies
                ), 5
            )
            movieGroupView.registerListener(this)
            recommendedFL.addView(movieGroupView.getRootView())
        } else {
            recommendedFL.visibility = View.GONE
        }
    }

    //Callbacks-----------------------------------------------------------------------------------
    override fun onMovieCardClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieClicked(movieID)
        }
    }

    override fun onMovieSeeAllClicked(groupType: GroupType) {
        listeners.forEach {
            it.onSeeAllRecommendedSimilarMoviesClicked(
                groupType,
                this.movieDetail.details.id,
                this.movieDetail.details.title
            )
        }
    }

    override fun onPeopleCardClicked(peopleID: Int, peopleType: PeopleType) {
        listeners.forEach {
            it.onPeopleCardClicked(peopleID, peopleType)
        }
    }

    override fun onPeopleSeeAllClicked(peopleType: PeopleType) {
        listeners.forEach {
            it.onPeopleSeeAllClicked(
                this.movieDetail.details.id,
                this.movieDetail.details.title,
                peopleType
            )
        }
    }

    override fun onRefresh() {
        listeners.forEach {
            it.onRefresh()
        }
    }

    override fun onRetryClicked() {
        listeners.forEach {
            it.onRetry()
        }
    }
}