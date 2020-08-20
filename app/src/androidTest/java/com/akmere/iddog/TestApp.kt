package com.akmere.iddog

import android.app.Application
import com.akmere.iddog.di.ApplicationModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            // declare used Android context
            androidContext(this@TestApp)
            // declare modules
            modules(listOf())
        }
    }
}