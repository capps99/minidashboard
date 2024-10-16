package com.minidashboard.app.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import minidashboard.composeapp.generated.resources.Res
import minidashboard.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
@Preview
fun HomeScreen(
    goToProjects: () -> Unit = {},
    goToMonitor: () -> Unit = {},
){
    MaterialTheme {
        val viewModel = koinViewModel<HomeViewModel>()
        var showContent by remember { mutableStateOf(false) }
        Column(
            Modifier.fillMaxWidth(),
               horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                showContent = !showContent
                viewModel.processAction(HomeActions.TapButton)
            }) {
                Text("Click me!")
            }
            Button(onClick = {
                viewModel.processAction(HomeActions.TapProjects)
                goToProjects()
            }) {
                Text("Go projects!")
            }
            Button(onClick = {
                viewModel.processAction(HomeActions.TapMonitor)
                goToMonitor()
            }) {
                Text("Go monitor!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { /*Greeting().greet()*/ }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}