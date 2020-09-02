package com.hadysalhab.movid.common.usecases

import com.google.gson.Gson
import com.hadysalhab.movid.networking.responses.TmdbErrorResponse

class ErrorMessageHandler(private val gson: Gson) {

    fun createErrorMessage(errMessage: String?) = when {
        errMessage == null -> {
            "Unable to retrieve data. Please try again.!"
        }
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
