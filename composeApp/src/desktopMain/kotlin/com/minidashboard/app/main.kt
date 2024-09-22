package com.minidashboard.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.minidashboard.app.di.initKoin
import com.minidashboard.app.presentation.App
import moe.tlaster.precompose.PreComposeApp

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "MiniDashboard",
    ) {
        PreComposeApp {
            App()
        }
    }
}