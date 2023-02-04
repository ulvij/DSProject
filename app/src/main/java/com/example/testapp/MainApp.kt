package com.example.testapp

import android.app.Application
import com.example.testapp.initializers.AppInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApp : Application() {

    @Inject
    lateinit var initializers: AppInitializer

    override fun onCreate() {
        super.onCreate()
        initializers.init(this)
    }
}