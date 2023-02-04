package com.example.testapp.di

import com.example.testapp.initializers.AppInitializer
import com.example.testapp.initializers.AppInitializers
import com.example.testapp.initializers.TimberInitializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideTimberInitializer(): TimberInitializer {
        return TimberInitializer()
    }

    @Provides
    fun provideAppInitializer(timberInitializer: TimberInitializer): AppInitializer {
        return AppInitializers(timberInitializer)
    }

}