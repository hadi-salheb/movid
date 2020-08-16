package com.hadysalhab.movid.common.time

class TimeProvider {
    val currentTimestamp: Long
        get() = System.currentTimeMillis()
}