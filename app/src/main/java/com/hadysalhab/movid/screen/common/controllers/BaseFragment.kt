package com.hadysalhab.movid.screen.common.controllers

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
     val activityComponent
        get() = (requireActivity() as BaseActivity).activityComponent

}