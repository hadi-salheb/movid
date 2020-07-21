package com.hadysalhab.movid

import android.os.Bundle
import android.widget.FrameLayout
import com.hadysalhab.movid.screen.common.controllers.BaseActivity
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost

class AuthActivity : BaseActivity(),FragmentFrameHost {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun getFragmentFrame(): FrameLayout {
        TODO("Not yet implemented")
    }
}