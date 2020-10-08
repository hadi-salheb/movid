package com.hadysalhab.movid.movies.usecases.search

import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class SearchMovieUseCase(
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val tmdbApi: TmdbApi,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val errorMessageHandler: ErrorMessageHandler
) : BaseBusyObservable<SearchMovieUseCase.Listener>() {
    interface Listener {
        fun onSearchMovieSuccess(movies: MoviesResponse, query: String)
        fun onSearchMovieFailure(msg: String, query: String)
    }

    private lateinit var query: String
    private var page: Int? = null
    private val LOCK = Object()

    fun searchMovieUseCase(query: String, page: Int) {
        assertNotBusyAndBecomeBusy()
        synchronized(LOCK) {
            this.query = query
            this.page = page
        }
        backgroundThreadPoster.post {
            val response = searchMovieFromApi()
            handleResponse(response)
        }
    }

    private fun searchMovieFromApi() = try {
        val response = tmdbApi.searchMovie(query = query, page = page!!).execute()
        ApiResponse.create(response)
    } catch (err: Throwable) {
        ApiResponse.create<MoviesResponseSchema>(err)
    }


    private fun handleResponse(response: ApiResponse<MoviesResponseSchema>) {
        when (response) {
            is ApiSuccessResponse -> {
                val result = schemaToModelHelper.getMoviesResponseFromSchema(
                    GroupType.SEARCH.apply {
                        value = this@SearchMovieUseCase.query
                    },
                    response.body
                )
                notifySuccess(
                    result
                )
            }
            is ApiErrorResponse, is ApiEmptyResponse -> {
                notifyFailure(errorMessageHandler.getErrorMessageFromApiResponse(response))
            }
        }
    }

    // notify controller
    private fun notifyFailure(error: String) {
        uiThreadPoster.post {
            listeners.forEach {
                it.onSearchMovieFailure(error, this.query)
            }
            becomeNotBusy()
        }
    }

    // notify controller
    private fun notifySuccess(movies: MoviesResponse) {
        uiThreadPoster.post {
            listeners.forEach {
                it.onSearchMovieSuccess(movies, this.query)
            }
            becomeNotBusy()
        }
    }

}