package com.lifeapp

import android.app.Application
import com.lifeapp.data.AppDatabase

class LifeApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    companion object {
        lateinit var instance: LifeApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
