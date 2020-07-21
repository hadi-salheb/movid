package com.hadysalhab.movid.screen.common.fragmentframe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseViewMvc

class FragmentFrameView(inflater: LayoutInflater, parent: ViewGroup?) : BaseViewMvc() {
    private val fragmentFrame: FrameLayout

    init {
        setRootView(inflater.inflate(R.layout.layout_fragment_frame, parent, false))
        fragmentFrame = findViewById(R.id.fragment_frame)
    }

    fun getFragmentFrame() = fragmentFrame

}