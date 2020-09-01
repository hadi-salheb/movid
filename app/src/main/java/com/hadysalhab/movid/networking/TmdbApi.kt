package com.hadysalhab.movid.networking

import com.hadysalhab.movid.BuildConfig
import com.hadysalhab.movid.networking.responses.*
import retrofit2.Call
import retrofit2.http.*

interface TmdbApi {
    @GET("/3/authentication/token/new?api_key=9a1a4d8d07b89f0c57458dbaf6d58a99")
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
    @FormUrlEncoded
    fun markAsFavorite(
        @Path("account_id") accountID: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("session_id") sessionID: String,
        @Field("media_type") mediaType: String = "movie",
        @Field("media_id") media_id: Int,
        @Field("favorite") favorite: Boolean
    ): Call<AddToFavResponse>

}