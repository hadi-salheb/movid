package com.hadysalhab.movid.common.utils

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Base class for Java Observable which needs to be aware of whether it's "busy" and expose this information to its clients
 * @see  "https://gist.github.com/techyourchance/44670734917d4ce085224a62cb9edf81"
 */

abstract class BaseBusyObservable<LISTENER_CLASS> :
    BaseObservable<LISTENER_CLASS>() {
    private val mIsBusy: AtomicBoolean = AtomicBoolean(false)
    val isBusy: Boolean
        get() = mIsBusy.get()

    /**
     * Atomically assert not busy and become busy
     * @throws IllegalStateException if wasn't busy when this method was called
     */
    protected fun assertNotBusyAndBecomeBusy() {
        check(mIsBusy.compareAndSet(false, true)) { "assertion violation: mustn't be busy" }
    }

    /**
     * Atomically check whether not busy and become busy
     * @return true if was "free" when this method was called; false if was busy
     */
    protected val isFreeAndBecomeBusy: Boolean
        protected get() = mIsBusy.compareAndSet(false, true)

    /**
     * Unconditionally become not busy
     */
    protected fun becomeNotBusy() {
        mIsBusy.set(false)
    }
}