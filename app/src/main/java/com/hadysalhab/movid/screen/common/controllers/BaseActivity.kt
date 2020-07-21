package com.hadysalhab.movid.screen.common.controllers

import androidx.appcompat.app.AppCompatActivity
import com.android.roam.wheelycool.dependencyinjection.presentation.ActivityModule
import com.hadysalhab.movid.common.MyApplication
import com.hadysalhab.movid.common.di.activity.ActivityComponent

abstract class BaseActivity : AppCompatActivity() {
    private val appComponent
        get() = (application as MyApplication).applicationComponent

    val activityComponent: ActivityComponent by lazy {
        appComponent.newActivityComponent(ActivityModule(this))
    }
}