package com.hadysalhab.movid.common

import android.content.Context
import android.os.Build

class DeviceConfigManager(private val context: Context) {
    fun getISO3166CountryCodeOrUS(): String {
        val re = "^[A-Z]{2}$".toRegex()
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0].country
        } else {
            context.resources.configuration.locale.country
        }
        return if (locale.matches(re)) {
            locale
        } else {
            "US"
        }
    }
}