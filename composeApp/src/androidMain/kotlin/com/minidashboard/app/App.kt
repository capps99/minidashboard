package com.minidashboard.app

import android.app.Application
import com.minidashboard.app.di.initKoin

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

}