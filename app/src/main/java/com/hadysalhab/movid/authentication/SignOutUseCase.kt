package com.hadysalhab.movid.authentication

import com.hadysalhab.movid.account.AccountResponse
import com.hadysalhab.movid.account.UserStateManager
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.movies.DiscoverMoviesFilterStateStore
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.persistence.AccountDao
import com.hadysalhab.movid.screen.main.filter.FilterState
import com.techyourchance.threadposter.BackgroundThreadPoster


class SignOutUseCase(
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val accountDao: AccountDao,
    private val moviesStateManager: MoviesStateManager,
    private val userStateManager: UserStateManager,
    private val filterStateStore: DiscoverMoviesFilterStateStore
) {
    fun signOutUser(accountResponse: AccountResponse) {
        //throw exception if clients tries to trigger this flow while it is busy
        backgroundThreadPoster.post {
            accountDao.deleteAccountResponse(accountResponse)
            moviesStateManager.clearMovies()
            userStateManager.clearData()
            filterStateStore.updateStoreState(FilterState())
            sharedPreferencesManager.setStoredSessionId("")
        }
    }
}