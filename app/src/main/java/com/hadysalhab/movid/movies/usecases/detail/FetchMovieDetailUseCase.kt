package com.hadysalhab.movid.movies.usecases.detail

import android.util.Log
import com.google.gson.Gson
import com.hadysalhab.movid.common.constants.BACKDROP_SIZE_780
import com.hadysalhab.movid.common.constants.IMAGES_BASE_URL
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.*
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.*
import retrofit2.Call
import retrofit2.Response

/**
 * UseCase that fetch popular,top-rated,upcoming movies
 * Notifies the client for success (if all calls succeeded), for failure (if only one calls fails)
 * */

class FetchMovieDetailUseCase(
    private val tmdbApi: TmdbApi,
    private val gson: Gson,
    private val moviesStateManager: MoviesStateManager
) :
    BaseBusyObservable<FetchMovieDetailUseCase.Listener>() {
    interface Listener {
        fun onFetchMovieDetailSuccess(movieDetail: MovieDetail)
        fun onFetchMovieDetailFailed(msg: String)
        fun onFetchingMovieDetail()
    }

    private lateinit var errorMessage: String

    fun fetchMovieDetailAndNotify(movieId: Int, sessionId: String) {
        Log.d("MoviesStateManager", "fetchMovieDetailAndNotify: ${moviesStateManager.isMovieDetailAvailable(movieId)}")
        if (moviesStateManager.isMovieDetailAvailable(movieId)) {
            notifySuccess(moviesStateManager.movieDetailList.find { it.details.id == movieId }!!)
        } else {
            listeners.forEach {
                it.onFetchingMovieDetail()
            }
            // will throw an exception if a client triggered this flow while it is busy
            assertNotBusyAndBecomeBusy()
            tmdbApi.fetchMovieDetail(
                id = movieId,
                sessionID = sessionId
            ).enqueue(object : retrofit2.Callback<MovieDetailResponse> {
                override fun onFailure(call: Call<MovieDetailResponse>, t: Throwable) {
                    createErrorMessage(t.message ?: "Unable to resolve host")
                    notifyFailure(errorMessage)
                }

                override fun onResponse(
                    call: Call<MovieDetailResponse>,
                    response: Response<MovieDetailResponse>
                ) {
                    if (response.body() == null || response.code() == 204) {
                        createErrorMessage("")
                    }
                    val movieDetail = getMovieDetails(response)
                    moviesStateManager.addMovieDetailToList(movieDetail)
                    notifySuccess(movieDetail)
                }
            })
        }
    }

    private fun getMovieDetails(response: Response<MovieDetailResponse>) = with(response.body()!!) {
        MovieDetail(
            getMovieInfo(this),
            getCredits(credits),
            getReviews(reviews),
            getImages(images),
            getAccountState(accountStates),
            getMoviesResponse(similar,GroupType.SIMILAR_MOVIES),
            getMoviesResponse(recommendations,GroupType.RECOMMENDED_MOVIES)
        )
    }


    private fun getMovieInfo(movieDetailResponse: MovieDetailResponse) = with(movieDetailResponse) {
        MovieInfo(
            adult,
            backdropPath,
            budget,
            getGenres(genres),
            homepage,
            id,
            imdbID,
            originalLanguage,
            overview,
            popularity,
            posterPath,
            releaseDate,
            revenue,
            runtime,
            status,
            tagLine,
            title,
            voteAvg,
            voteCount
        )
    }

    private fun getGenres(genresSchema: List<GenresSchema>) = genresSchema.map { el ->
        Genres(el.id, el.name)
    }

    private fun getCredits(creditsSchema: CreditsSchema) = with(creditsSchema) {
        Credits(id, getCasts(cast), getCrews(crew))
    }

    private fun getCasts(castSchemas: List<CastSchema>): List<Cast> = castSchemas.map { el ->
        Cast(
            el.castID,
            el.character,
            el.creditID,
            el.id,
            el.name,
            el.profilePath
        )
    }

    private fun getCrews(crewSchemas: List<CrewSchema>): List<Crew> = crewSchemas.map { el ->
        Crew(
            el.creditID,
            el.department,
            el.id,
            el.job,
            el.name,
            el.profilePath
        )
    }

    private fun getReviews(reviewsSchema: ReviewsSchema) = with(reviewsSchema) {
        Reviews(
            id,
            page,
            getReview(review),
            totalPages,
            totalResults
        )
    }

    private fun getReview(reviewSchema: List<ReviewSchema>) = reviewSchema.map { el ->
        Review(el.id, el.author, el.content, el.url)
    }

    private fun getImages(imagesSchema: ImagesSchema) = with(imagesSchema) {
        Images(getBackdrops(backdrops))
    }

    private fun getBackdrops(backdrops: List<BackdropsSchema>) = backdrops.map { el ->
        Backdrops(IMAGES_BASE_URL + BACKDROP_SIZE_780 + el.filePath)
    }

    private fun getAccountState(accountStatesSchema: AccountStatesSchema) =
        with(accountStatesSchema) {
            AccountStates(id, favorite, watchlist)
        }

    private fun getMoviesResponse(moviesResponseSchema: MoviesResponseSchema,tag:GroupType) = with(moviesResponseSchema) {
        MoviesResponse(
            page,
            totalResults,
            total_pages,
            getMovies(movies),
            tag
        )
    }

    private fun getMovies(moviesSchema: List<MovieSchema>): MutableList<Movie> {
        val movies = mutableListOf<Movie>()
        movies.addAll(moviesSchema.map { movieSchema ->
            with(movieSchema) {
                Movie(
                    id,
                    title,
                    posterPath,
                    backdropPath,
                    voteAvg,
                    voteCount,
                    releaseDate,
                    overview
                )
            }
        })
        return movies
    }

    private fun createErrorMessage(errMessage: String) {
        this.errorMessage = when {
            errMessage.contains("status_message") -> {
                gson.fromJson(errMessage, TmdbErrorResponse::class.java).statusMessage
            }
            errMessage.contains("Unable to resolve host") -> {
                "Please check network connection and try again"
            }
            else -> {
                "Unable to retrieve data. Please try again.!"
            }
        }
    }

    private fun notifyFailure(msg: String) {
        // notify controller
        listeners.forEach {
            it.onFetchMovieDetailFailed(msg)
        }
        becomeNotBusy()
    }

    private fun notifySuccess(movieDetail: MovieDetail) {
        // notify controller
        listeners.forEach {
            it.onFetchMovieDetailSuccess(movieDetail)
        }
        becomeNotBusy()
    }
}
