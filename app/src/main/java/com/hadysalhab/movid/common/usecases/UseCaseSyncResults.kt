package com.hadysalhab.movid.common.usecases

sealed class UseCaseSyncResults<T> {
    data class Error<T>(val errMessage: String) : UseCaseSyncResults<T>()
    data class Results<T>(val data: T) : UseCaseSyncResults<T>()
}