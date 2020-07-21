package com.hadysalhab.movid.screen.common.screensnavigator

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.hadysalhab.movid.screen.common.fragmentframehost.FragmentFrameHost
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavController.RootFragmentListener

class AuthNavigator(
    private val fragmentManager: FragmentManager,
    private val fragmentFrameHost: FragmentFrameHost
) {
    lateinit var fragNavController: FragNavController

    private val rootFragmentListener: RootFragmentListener = object : RootFragmentListener {
        override val numberOfRootFragments: Int
            get() = 1

        override fun getRootFragment(index: Int): Fragment {
            return when (index) {
                else -> throw IllegalStateException("unsupported tab index: $index")
            }
        }
    }

    fun init(savedInstanceState: Bundle) {
        fragNavController =
            FragNavController(fragmentManager, fragmentFrameHost.getFragmentFrame().id)
        fragNavController.rootFragmentListener = rootFragmentListener
        fragNavController.initialize(FragNavController.TAB1, savedInstanceState)
    }

    fun onSavedInstanceState(savedInstanceState: Bundle){
        fragNavController.onSaveInstanceState(savedInstanceState)
    }

}