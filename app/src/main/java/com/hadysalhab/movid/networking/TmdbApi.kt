package com.hadysalhab.movid.networking

import com.hadysalhab.movid.BuildConfig
import com.hadysalhab.movid.networking.responses.CreateAndSignTokenResponse
import com.hadysalhab.movid.networking.responses.CreateSessionResponse
import com.hadysalhab.movid.networking.responses.MoviesResponse
import retrofit2.Call
import retrofit2.http.*

interface TmdbApi {
    @GET("/3/authentication/token/new?api_key=9a1a4d8d07b89f0c57458dbaf6d58a99")
    fun createRequestToken(): Call<CreateAndSignTokenResponse>

    @POST("/3/authentication/token/validate_with_login?api_key=${BuildConfig.API_KEY}")
    @FormUrlEncoded
    fun signToken(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("request_token") requestToken: String
    ):  Call<CreateAndSignTokenResponse>


    @POST("/3/authentication/session/new?api_key=${BuildConfig.API_KEY}")
    @FormUrlEncoded
    fun createSession(
        @Field("request_token") requestToken: String
    ):  Call<CreateSessionResponse>


    @GET("/3/movie/popular")
    fun fetchPopularMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("region") region:String
    ): Call<MoviesResponse>

    @GET("/3/movie/top_rated")
    fun fetchTopRatedMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("region") region:String
    ): Call<MoviesResponse>

    @GET("/3/movie/upcoming")
    fun fetchUpcomingMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("region") region:String
    ): Call<MoviesResponse>

    @GET("/3/movie/now_playing")
    fun fetchNowPlayingMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("region") region:String
    ): Call<MoviesResponse>

    @GET("/3/movie/latest")
    fun fetchLatestMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Call<MoviesResponse>




}