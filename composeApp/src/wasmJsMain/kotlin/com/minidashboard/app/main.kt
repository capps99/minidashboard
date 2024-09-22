package com.minidashboard.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.minidashboard.app.di.initKoin
import com.minidashboard.app.presentation.App
import kotlinx.browser.document
import moe.tlaster.precompose.PreComposeApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    ComposeViewport(document.body!!) {
        PreComposeApp {
            App()
        }
    }
}