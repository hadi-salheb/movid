package com.hadysalhab.movid.screen.common.views

import android.content.Context
import android.view.View
import androidx.annotation.StringRes

abstract class BaseViewMvc : ViewMvc {
    private var mRootView: View? = null

    override fun getRootView(): View = mRootView!!.rootView


    protected open fun setRootView(rootView: View?) {
        mRootView = rootView
    }

    protected open fun <T : View?> findViewById(id: Int): T = getRootView().findViewById(id)


    protected open fun getContext(): Context = getRootView().context


    protected open fun getString(@StringRes id: Int): String? = getContext().getString(id)

}