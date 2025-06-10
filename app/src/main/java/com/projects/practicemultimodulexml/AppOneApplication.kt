package com.projects.practicemultimodulexml

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppOneApplication : Application() {

    override fun onCreate() {
        super.onCreate()

    }
}