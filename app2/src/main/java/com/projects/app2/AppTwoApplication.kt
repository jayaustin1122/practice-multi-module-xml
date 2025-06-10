package com.projects.app2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppTwoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

    }
}