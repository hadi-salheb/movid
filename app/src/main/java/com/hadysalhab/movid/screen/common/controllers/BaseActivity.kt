package com.hadysalhab.movid.screen.common.controllers

import androidx.appcompat.app.AppCompatActivity
import com.android.roam.wheelycool.dependencyinjection.presentation.PresentationModule
import com.hadysalhab.movid.common.MyApplication
import com.hadysalhab.movid.common.di.activity.ActivityModule

abstract class BaseActivity : AppCompatActivity() {
    private val appComponent
        get() = (application as MyApplication).appComponent

    val activityComponent by lazy {
        appComponent.newActivityComponent(ActivityModule(this))
    }

    private val presentationComponent by lazy {
        activityComponent.newPresentationComponent(PresentationModule(this))
    }

    protected val injector get() = presentationComponent
}