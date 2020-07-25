package com.hadysalhab.movid.screen.main

import android.os.Bundle
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.controllers.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)
    }
}