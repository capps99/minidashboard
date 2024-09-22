package com.minidashboard.app

import androidx.compose.ui.window.ComposeUIViewController
import com.minidashboard.app.presentation.App
import moe.tlaster.precompose.PreComposeApp

fun MainViewController() = ComposeUIViewController {
    PreComposeApp {
        App()
    }
}