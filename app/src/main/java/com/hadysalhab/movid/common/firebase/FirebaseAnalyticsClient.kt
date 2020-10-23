package com.hadysalhab.movid.common.firebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.hadysalhab.movid.screen.main.BottomNavigationItems
import com.hadysalhab.movid.screen.main.featuredgroups.ToolbarCountryItems

class FirebaseAnalyticsClient(
    private val context: Context,
    private val firebaseAnalytics: FirebaseAnalytics
) {
    fun logTabTransaction(bottomNavigationItems: BottomNavigationItems) {
        val tabTransactionBundle = Bundle().also {
            it.putString("BottomNavigationItem", "$bottomNavigationItems")
        }
        firebaseAnalytics.logEvent("TabTransaction", tabTransactionBundle)
    }

    fun logFragmentTransaction(fragmentName: String) {
        val fragmentTransactionBundle = Bundle().also {
            it.putString("Fragment", fragmentName)
        }
        firebaseAnalytics.logEvent("FragmentTransaction", fragmentTransactionBundle)
    }

    fun logCastClick() {
        firebaseAnalytics.logEvent("CastCheck", null)
    }

    fun logFlagChange(toolbarCountryItem: ToolbarCountryItems) {
        val flagBundle = Bundle().also {
            it.putString("CountryName", toolbarCountryItem.countryName)
        }
        firebaseAnalytics.logEvent("FlagChange", flagBundle)
    }

    fun logLogin() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, null)
    }

    fun logLogOut() {
        firebaseAnalytics.logEvent("Logout", null)
    }

    fun logMainActivityProcessDeath() {
        firebaseAnalytics.logEvent("MainScreenProcessDeath", null)

    }

    fun logPagination(tag: String, page: Int) {
        val paginationBundle = Bundle()
        paginationBundle.apply {
            putString("Tag", tag)
            putString("Page", page.toString())
        }
        firebaseAnalytics.logEvent("Pagination", paginationBundle)
    }

    fun logSearch(query: String) {
        val searchBundle = Bundle()
        searchBundle.apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, query)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, searchBundle)

    }

    fun logFilterSuccess() {
        firebaseAnalytics.logEvent("FilterSuccess", null)
    }

    fun logFilterFailure(cause: String) {
        val filterBundle = Bundle().also {
            it.putString("Cause", cause)
        }
        firebaseAnalytics.logEvent("FilterFailure", filterBundle)
    }

    fun logFavorite() {
        firebaseAnalytics.logEvent("Favorites", null)
    }

    fun logWatchlist() {
        firebaseAnalytics.logEvent("Watchlist", null)
    }

    fun logRateMovid() {
        val rateBundle = Bundle()
        rateBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Movid App")
        firebaseAnalytics.logEvent("RateMovid", rateBundle)
    }

    fun logShareMovid() {
        val shareBundle = Bundle()
        shareBundle.apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Movid App")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, shareBundle)
    }

    fun logUseCaseError(message: String) {
        val useCaseErrorBundle = Bundle().also {
            it.putString("ErrorMessage", message)
        }
        firebaseAnalytics.logEvent("UseCaseFailure", useCaseErrorBundle)
    }
}