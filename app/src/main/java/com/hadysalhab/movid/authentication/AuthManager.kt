package com.hadysalhab.movid.authentication

import com.hadysalhab.movid.account.usecases.session.GetSessionIdUseCaseSync

class AuthManager(private val getSessionIdUseCaseSync: GetSessionIdUseCaseSync) {
    fun isUserAuthenticated(): Boolean =
        getSessionIdUseCaseSync.getSessionIdUseCaseSync().isNotEmpty()

}