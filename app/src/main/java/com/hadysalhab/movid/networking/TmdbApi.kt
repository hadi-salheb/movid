package com.hadysalhab.movid.networking

import com.hadysalhab.movid.BuildConfig
import com.hadysalhab.movid.account.usecases.favorite.FavoriteHttpBodyRequest
import com.hadysalhab.movid.account.usecases.watchlist.WatchlistHttpBodyRequest
import com.hadysalhab.movid.networking.responses.*
import retrofit2.Call
import retrofit2.http.*

interface TmdbApi {
    @GET("/3/authentication/token/new?api_key=${BuildConfig.API_KEY}")
    fun createRequestToken(): Call<CreateAndSignTokenResponse>

    @GET("/3/account")
    fun getAccountDetail(
        @Query("session_id") sessionID: String,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Call<AccountSchema>

    @POST("/3/authentication/token/validate_with_login?api_key=${BuildConfig.API_KEY}")
    @FormUrlEncoded
    fun signToken(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("request_token") requestToken: String
    ): Call<CreateAndSignTokenResponse>


    @POST("/3/authentication/session/new?api_key=${BuildConfig.API_KEY}")
    @FormUrlEncoded
    fun createSession(
        @Field("request_token") requestToken: String
    ): Call<CreateSessionResponse>


    @GET("/3/movie/popular")
    fun fetchPopularMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("region") region: String
    ): Call<MoviesResponseSchema>

    @GET("/3/movie/top_rated")
    fun fetchTopRatedMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("region") region: String
    ): Call<MoviesResponseSchema>

    @GET("/3/movie/upcoming")
    fun fetchUpcomingMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("region") region: String
    ): Call<MoviesResponseSchema>

    @GET("/3/movie/now_playing")
    fun fetchNowPlayingMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("region") region: String
    ): Call<MoviesResponseSchema>

    @GET("/3/movie/latest")
    fun fetchLatestMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Call<MoviesResponseSchema>

    @GET("/3/movie/{movie_id}/similar")
    fun fetchSimilarMovies(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Call<MoviesResponseSchema>

    @GET("/3/movie/{movie_id}/recommendations")
    fun fetchRecommendedMovies(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Call<MoviesResponseSchema>

    @GET("/3/movie/{movie_id}")
    fun fetchMovieDetail(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") details: String = "recommendations,videos,credits,reviews,images,release_dates,account_states,similar",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("session_id") sessionID: String
    ): Call<MovieDetailSchema>

    @GET("/3/movie/{movie_id}/reviews")
    fun fetchReviewsForMovie(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Call<ReviewsSchema>

    @POST("/3/account/{account_id}/favorite")
    fun markAsFavorite(
        @Path("account_id") accountID: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("session_id") sessionID: String,
        @Body httpBodyRequest: FavoriteHttpBodyRequest
    ): Call<WatchlistFavoriteResponse>

    @POST("/3/account/{account_id}/watchlist")
    fun addToWatchlist(
        @Path("account_id") accountID: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("session_id") sessionID: String,
        @Body httpBodyRequest: WatchlistHttpBodyRequest
    ): Call<WatchlistFavoriteResponse>

    @GET("/3/account/{account_id}/favorite/movies")
    fun getFavoriteMovies(
        @Path("account_id") accountID: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("session_id") sessionID: String,
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "created_at.desc"
    ): Call<MoviesResponseSchema>

    @GET("/3/account/{account_id}/watchlist/movies")
    fun getWatchlistMovies(
        @Path("account_id") accountID: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("session_id") sessionID: String,
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "created_at.desc"
    ): Call<MoviesResponseSchema>

    @GET("/3/collection/{collection_id}")
    fun getCollection(
        @Path("collection_id") collectionID: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Call<CollectionSchema>

    @GET("/3/search/movie")
    fun searchMovie(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Call<MoviesResponseSchema>

    @GET("/3/discover/movie")
    fun discover(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String,
        @Query("include_adult") includeAdult: Boolean,
        @Query("primary_release_date.gte") primaryReleaseDateGte: String?,
        @Query("primary_release_date.lte") primaryReleaseDateLte: String?,
        @Query("vote_count.gte") voteCountGte: Int?,
        @Query("vote_count.lte") voteCountLte: Int?,
        @Query("vote_average.gte") voteAverageGte: Float?,
        @Query("vote_average.lte") voteAverageLte: Float?,
        @Query("with_genres") withGenres: String,
        @Query("with_runtime.gte") withRuntimeGte: Int?,
        @Query("with_runtime.lte") withRuntimeLte: Int?
    ): Call<MoviesResponseSchema>

    @GET("/3/movie/{movie_id}/credits")
    fun getCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Call<CreditsSchema>
}