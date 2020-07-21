package com.hadysalhab.movid.screen.common

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hadysalhab.movid.screen.common.fragmentframe.FragmentFrameView
import com.hadysalhab.movid.screen.layout.launcher.LauncherViewImpl

class ViewFactory(private val layoutInflater: LayoutInflater) {
    fun getFragmentFrameView(parent: ViewGroup?) = FragmentFrameView(layoutInflater, parent)
    fun getLauncherView(parent: ViewGroup?) = LauncherViewImpl(layoutInflater,parent)
}