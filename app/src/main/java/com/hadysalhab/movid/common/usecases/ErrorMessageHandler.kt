package com.hadysalhab.movid.common.usecases

import com.google.gson.Gson
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.responses.TmdbErrorResponse

class ErrorMessageHandler(private val gson: Gson) {

    private fun createErrorMessage(errMessage: String?) = when {
        errMessage == null -> {
            "Internal Server Error. Please try again.!"
        }
        errMessage.contains("status_message") -> {
            gson.fromJson(errMessage, TmdbErrorResponse::class.java).statusMessage
        }
        errMessage.contains("Unable to resolve host") -> {
            "No Internet found. Check your connection and try again."
        }
        else -> {
            "Unable to retrieve data. Please try again.!"
        }
    }

    fun getErrorMessageFromApiResponse(response: ApiResponse<*>): String = when (response) {
        is ApiEmptyResponse -> createErrorMessage(null)
        is ApiErrorResponse -> createErrorMessage(response.errorMessage)
        else -> throw RuntimeException("Cannot retrieve error message from a successful response")
    }
}
