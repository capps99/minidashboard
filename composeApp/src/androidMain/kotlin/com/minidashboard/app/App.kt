package com.minidashboard.app

import android.app.Application
import android.content.Context
import com.minidashboard.app.di.initKoin
import org.koin.dsl.module

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        initKoin(
            additionalModules = listOf(
                module {
                    single<Context> { applicationContext }
                }
            )
        )
    }
}