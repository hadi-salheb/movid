package com.hadysalhab.movid.screen.common.views

import android.content.Context
import android.view.View
import androidx.annotation.StringRes

abstract class BaseViewMvc : ViewMvc {
    private var mRootView: View? = null

    override fun getRootView(): View = mRootView!!.rootView


    protected fun setRootView(rootView: View?) {
        mRootView = rootView
    }

    protected fun <T : View?> findViewById(id: Int): T {
        return getRootView().findViewById<T>(id)
    }


    protected fun getContext(): Context = getRootView().context


    protected fun getString(@StringRes id: Int): String? = getContext().getString(id)

}