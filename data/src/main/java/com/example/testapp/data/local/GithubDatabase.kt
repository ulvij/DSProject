package com.example.testapp.data.local

import android.content.Context
import androidx.room.*
import com.example.testapp.data.local.models.ContributorLocalDto

@Database(entities = [ContributorLocalDto::class], version = 1)
abstract class GithubDatabase : RoomDatabase() {

    abstract fun githubDao(): GithubDao

    companion object {
        private const val DB_NAME = "contributors_database"

        @Volatile
        private var INSTANCE: GithubDatabase? = null

        fun getInstance(context: Context): GithubDatabase {

            INSTANCE?.let { return it }

            synchronized(this) {
                INSTANCE?.let { return it }

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GithubDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}