package com.example.testapp.initializers

import android.app.Application

interface AppInitializer {
    fun init(application: Application)
}
