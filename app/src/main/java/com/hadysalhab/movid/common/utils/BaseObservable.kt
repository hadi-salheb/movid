package com.hadysalhab.movid.common.utils

import java.util.*


/**
 * Base class for Java Observable
 * @see  "https://gist.github.com/techyourchance/6602815188294c1c58779d3e8d16f12b"
 */

abstract class BaseObservable<LISTENER_CLASS> {
    private val MONITOR = Any()
    private val mListeners: MutableSet<LISTENER_CLASS> =
        HashSet()

    fun registerListener(listener: LISTENER_CLASS) {
        synchronized(MONITOR) {
            val hadNoListeners = mListeners.size == 0
            mListeners.add(listener)
            if (hadNoListeners && mListeners.size == 1) {
                onFirstListenerRegistered()
            }
        }
    }

    fun unregisterListener(listener: LISTENER_CLASS) {
        synchronized(MONITOR) {
            val hadOneListener = mListeners.size == 1
            mListeners.remove(listener)
            if (hadOneListener && mListeners.size == 0) {
                onLastListenerUnregistered()
            }
        }
    }

    protected val listeners: Set<LISTENER_CLASS>
         get() {
            synchronized(
                MONITOR
            ) {
                return Collections.unmodifiableSet(
                    HashSet(mListeners)
                )
            }
        }

    protected fun onFirstListenerRegistered() {}
    protected fun onLastListenerUnregistered() {}
}