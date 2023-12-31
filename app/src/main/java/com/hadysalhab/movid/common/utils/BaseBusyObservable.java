package com.hadysalhab.movid.common.utils;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseBusyObservable<LISTENER_CLASS> extends BaseObservable<LISTENER_CLASS> {

    private final AtomicBoolean mIsBusy = new AtomicBoolean(false);

    public final boolean isBusy() {
        return mIsBusy.get();
    }

    /**
     * Atomically assert not busy and become busy
     *
     * @throws IllegalStateException if wasn't busy when this method was called
     */
    protected final void assertNotBusyAndBecomeBusy() {
        if (!mIsBusy.compareAndSet(false, true)) {
            throw new IllegalStateException("assertion violation: mustn't be busy");
        }
    }

    /**
     * Atomically check whether not busy and become busy
     *
     * @return true if was "free" when this method was called; false if was busy
     */
    protected final boolean isFreeAndBecomeBusy() {
        return mIsBusy.compareAndSet(false, true);
    }

    /**
     * Unconditionally become not busy
     */
    protected final void becomeNotBusy() {
        mIsBusy.set(false);
    }
}