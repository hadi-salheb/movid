package com.hadysalhab.movid.screen.common.controllers

import androidx.fragment.app.Fragment
import com.android.roam.wheelycool.dependencyinjection.presentation.PresentationModule

abstract class BaseFragment : Fragment() {
    private val presentationComponent by lazy {
        (requireActivity() as BaseActivity).activityComponent.newPresentationComponent(
            PresentationModule(this)
        )
    }

    protected val injector get() = presentationComponent

}