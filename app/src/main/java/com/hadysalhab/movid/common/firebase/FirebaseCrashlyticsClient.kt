package com.hadysalhab.movid.common.firebase

import com.google.firebase.crashlytics.FirebaseCrashlytics

class FirebaseCrashlyticsClient(private val firebaseCrashlytics: FirebaseCrashlytics) {

    fun setUserId(userId: String) {
        firebaseCrashlytics.setUserId(userId)
    }

}