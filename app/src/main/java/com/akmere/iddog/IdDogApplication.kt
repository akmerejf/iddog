package com.akmere.iddog

import android.app.Application
import com.akmere.iddog.di.ApplicationModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class IdDogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // start Koin!
        startKoin {
            // declare used Android context
            androidContext(this@IdDogApplication)
            // declare modules
            modules(
                ApplicationModules.networkModule,
                ApplicationModules.loginModule,
                ApplicationModules.dogFeedModule
            )
        }
    }
}