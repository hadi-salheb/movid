package com.hadysalhab.movid.screen.common.dialogs

import androidx.fragment.app.DialogFragment
import com.hadysalhab.movid.common.di.presentation.PresentationModule
import com.hadysalhab.movid.screen.common.controllers.BaseActivity

abstract class BaseDialog : DialogFragment() {
    private val presentationComponent by lazy {
        (requireActivity() as BaseActivity).activityComponent.newPresentationComponent(
            PresentationModule(this)
        )
    }

    protected val injector get() = presentationComponent

}