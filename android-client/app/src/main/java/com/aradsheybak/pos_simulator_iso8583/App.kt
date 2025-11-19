package com.aradsheybak.pos_simulator_iso8583

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class POSApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@POSApplication)
            modules(appModule)
        }
    }
}