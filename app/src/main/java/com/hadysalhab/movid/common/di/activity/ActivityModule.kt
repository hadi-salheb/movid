package com.android.roam.wheelycool.dependencyinjection.presentation

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: FragmentActivity) {

    @Provides
    fun getActivity(): FragmentActivity = activity

    @Provides
    fun getContext(activity: FragmentActivity): Context = activity

    @Provides
    fun getLayoutInflater(): LayoutInflater = LayoutInflater.from(activity)

    @Provides
    fun getFragmentManager(activity: FragmentActivity): FragmentManager =
        activity.supportFragmentManager
}